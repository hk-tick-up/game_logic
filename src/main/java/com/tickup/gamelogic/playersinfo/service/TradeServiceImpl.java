package com.tickup.gamelogic.playersinfo.service;

import com.tickup.gamelogic.exception.TradeException;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.playersinfo.domain.OwnedShares;

import com.tickup.gamelogic.playersinfo.domain.TradeLog;
import com.tickup.gamelogic.playersinfo.domain.TradeType;
import com.tickup.gamelogic.playersinfo.repository.CurrentPlayersInfoRepository;
import com.tickup.gamelogic.playersinfo.repository.OwnedSharesRepository;
import com.tickup.gamelogic.playersinfo.repository.TradeLogRepository;
import com.tickup.gamelogic.playersinfo.response.InvestmentDetail;
import com.tickup.gamelogic.playersinfo.response.MyInvestmentResponse;
import com.tickup.gamelogic.playersinfo.response.TradeResponse;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import com.tickup.gamelogic.stocksettings.repository.CompanyInfoRepository;
import com.tickup.gamelogic.stocksettings.repository.StockDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final CurrentPlayersInfoRepository playersInfoRepository;
    private final OwnedSharesRepository ownedSharesRepository;
    private final TradeLogRepository tradeLogRepository;
    private final GameRoomsRepository gameRoomsRepository;
    private final StockDataRepository stockDataRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CompanyInfoRepository companyInfoRepository;

    @Override
    public TradeResponse processTrade(Long gameRoomId, String userId, String ticker, int shares, TradeType tradeType) {
        log.info("Starting trade process: gameRoomId={}, userId={}, ticker={}, shares={}, tradeType={}",
                gameRoomId, userId, ticker, shares, tradeType);

        try {
            GameRooms gameRoom = getGameRoom(gameRoomId);
            CurrentPlayersInfo playerInfo = getPlayerInfo(gameRoom, userId);
            int currentTurn = gameRoom.getCurrentTurn();
            int pricePerShare = getStockPrice(gameRoom, currentTurn, ticker);
            int totalPrice = pricePerShare * shares;

            if (TradeType.BUY.equals(tradeType)) {
                handleBuy(gameRoom, playerInfo, ticker, shares, totalPrice, pricePerShare);
            } else if (TradeType.SELL.equals(tradeType)) {
                handleSell(gameRoom, playerInfo, ticker, shares, totalPrice, pricePerShare);
            } else {
                throw new IllegalArgumentException("거래 종류가 잘못되었습니다.");
            }

            updatePlayerStats(playerInfo, gameRoom);
            List<InvestmentDetail> investments = getInvestmentDetails(gameRoomId, userId);
            saveTradeLog(gameRoom, userId, ticker, shares, pricePerShare, tradeType, playerInfo.getBalance());

            return TradeResponse.from(playerInfo, investments, true, getTradeSummary(tradeType));
        } catch (IllegalArgumentException e) {
            log.error("Error during trade process: userId={}, gameRoomId={}, ticker={}, error={}",
                    userId, gameRoomId, ticker, e.getMessage());
            throw new TradeException("거래 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during trade process: userId={}, gameRoomId={}, ticker={}, error={}",
                    userId, gameRoomId, ticker, e.getMessage());
            throw new TradeException("예기치 않은 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    private GameRooms getGameRoom(Long gameRoomId) {
        return gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("게임 방 정보를 찾을 수 없습니다."));
    }

    private CurrentPlayersInfo getPlayerInfo(GameRooms gameRoom, String userId) {
        return playersInfoRepository.findByGameRoomIdAndUserId(gameRoom.getGameRoomsId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("플레이어 정보를 찾을 수 없습니다."));
    }

    private int getStockPrice(GameRooms gameRoom, int turn, String ticker) {
        List<Integer> prices = stockDataRepository.findStockPriceByGameRoomsIdAndTickerAndTurn(
                gameRoom.getGameRoomsId(), ticker, turn);
        log.info("Stock prices for gameRoomId={}, turn={}, ticker={}: {}", gameRoom.getGameRoomsId(), turn, ticker, prices);
        return prices.get(0);
    }

    private void handleBuy(GameRooms gameRoom, CurrentPlayersInfo playerInfo, String ticker, int shares, int totalPrice, int pricePerShare) {
        playerInfo.updateBalance(-totalPrice);
        OwnedShares ownedShares = getOrCreateOwnedShares(gameRoom, playerInfo.getUserId(), ticker);
        ownedShares.updateAveragePurchasePrice(shares, pricePerShare);
        ownedShares.addShares(shares);
    }

    private void handleSell(GameRooms gameRoom, CurrentPlayersInfo playerInfo, String ticker, int shares, int totalPrice, int pricePerShare) {
        OwnedShares ownedShares = getOrCreateOwnedShares(gameRoom, playerInfo.getUserId(), ticker);
        ownedShares.decreaseShares(shares);
        ownedShares.updateRateReturn(pricePerShare);

        if (ownedShares.getShares() == 0) {
            ownedSharesRepository.delete(ownedShares);
        }
        playerInfo.updateBalance(totalPrice);
    }

    private OwnedShares getOrCreateOwnedShares(GameRooms gameRoom, String userId, String ticker) {
        return ownedSharesRepository.findByGameRoomsGameRoomsIdAndUserIdAndTicker(
                        gameRoom.getGameRoomsId(), userId, ticker)
                .orElseGet(() -> createOwnedShares(gameRoom, userId, ticker));
    }

    private OwnedShares createOwnedShares(GameRooms gameRoom, String userId, String ticker) {
        log.info("Creating new OwnedShares: gameRoomId={}, userId={}, ticker={}", gameRoom.getGameRoomsId(), userId, ticker);
        OwnedShares newOwnedShares = OwnedShares.builder()
                .userId(userId)
                .ticker(ticker)
                .shares(0)
                .gameRooms(gameRoom)
                .build();
        return ownedSharesRepository.save(newOwnedShares);
    }

    private void updatePlayerStats(CurrentPlayersInfo playerInfo, GameRooms gameRoom) {
        List<OwnedShares> ownedShares = ownedSharesRepository.findAllByGameRoomsIdAndUserId(
                gameRoom.getGameRoomsId(), playerInfo.getUserId());
        List<StockData> currentPrices = stockDataRepository.findAllByGameRoomAndTurn(
                gameRoom.getGameRoomsId(), gameRoom.getCurrentTurn());

        updateOwnedSharesValues(ownedShares, currentPrices);
        playerInfo.updateValuationAmount(getStockValues(ownedShares, currentPrices));
        playerInfo.updateNetAssets();
        playerInfo.updateReturnRate();

        ownedSharesRepository.saveAll(ownedShares);
        playersInfoRepository.save(playerInfo);  // 이 부분이 없어서 변경사항이 DB에 반영되지 않았을 것입니다

    }

    private void updateOwnedSharesValues(List<OwnedShares> ownedShares, List<StockData> currentPrices) {
        ownedShares.forEach(share -> {
            int currentPrice = getCurrentPrice(currentPrices, share.getTicker());
            share.updateRateReturn(currentPrice);
        });
    }

    private int getCurrentPrice(List<StockData> currentPrices, String ticker) {
        return currentPrices.stream()
                .filter(price -> price.getTicker().equals(ticker))
                .findFirst()
                .map(StockData::getStockPrice)
                .orElse(0);
    }

    private List<Integer> getStockValues(List<OwnedShares> ownedShares, List<StockData> currentPrices) {
        return ownedShares.stream()
                .map(share -> share.getShares() * getCurrentPrice(currentPrices, share.getTicker()))
                .toList();
    }

    private List<InvestmentDetail> getInvestmentDetails(Long gameRoomId, String userId) {
        GameRooms gameRoom = getGameRoom(gameRoomId);
        List<OwnedShares> ownedShares = ownedSharesRepository.findAllByGameRoomsIdAndUserId(gameRoomId, userId);
        List<StockData> currentPrices = stockDataRepository.findAllByGameRoomAndTurn(
                gameRoomId, gameRoom.getCurrentTurn());

        return ownedShares.stream()
                .map(share -> InvestmentDetail.from(share, getCurrentPrice(currentPrices, share.getTicker())))
                .toList();
    }

    private void saveTradeLog(GameRooms gameRoom, String userId, String ticker, int shares, int price, TradeType tradeType, int balance) {
        String companyName = companyInfoRepository.findCompanyNameByGameRoomIdAndTicker(gameRoom.getGameRoomsId(), ticker);

        tradeLogRepository.save(
                TradeLog.builder()
                        .userId(userId)
                        .companyName(companyName)
                        .shares(shares)
                        .price(price)
                        .turn(gameRoom.getCurrentTurn())
                        .tradeType(tradeType)
                        .gameRooms(gameRoom)
                        .remainingFunds(balance)
                        .build()
        );
    }

    @Override
    public void sendTurnInvestmentUpdates(GameRooms gameRoom) {
//        gameRoom.getCurrentPlayersInfos().forEach(player -> {
//            List<InvestmentDetail> investments = getInvestmentDetails(gameRoom.getGameRoomsId(), player.getUserId());
//            MyInvestmentResponse response = MyInvestmentResponse.from(player, investments);
//
//            messagingTemplate.convertAndSend(
//                    "/topic/gameRoom/" + gameRoom.getGameRoomsId() + "/player/" + player.getUserId() + "/investments",
//                    response
//            );
//        });
        gameRoom.getCurrentPlayersInfos().forEach(player -> {
            // 플레이어 정보를 새로 조회 (영속성 컨텍스트에서 최신 상태로)
            CurrentPlayersInfo refreshedPlayer = playersInfoRepository.findById(player.getCurrentPlayersInfoId())
                    .orElseThrow(() -> new IllegalStateException("Player not found"));

            // 플레이어 투자 정보 업데이트
            updatePlayerStats(refreshedPlayer, gameRoom);

            List<InvestmentDetail> investments = getInvestmentDetails(gameRoom.getGameRoomsId(), refreshedPlayer.getUserId());
            MyInvestmentResponse response = MyInvestmentResponse.from(refreshedPlayer, investments);

            messagingTemplate.convertAndSend(
                    "/topic/gameRoom/" + gameRoom.getGameRoomsId() + "/player/" + refreshedPlayer.getUserId() + "/investments",
                    response
            );
        });
    }

    private String getTradeSummary(TradeType tradeType) {
        return tradeType == TradeType.BUY ? "주식 구매가 완료되었습니다." : "주식 판매가 완료되었습니다.";
    }

    @Override
    public MyInvestmentResponse getInvestments(Long gameRoomId, String userId) {
        GameRooms gameRoom = getGameRoom(gameRoomId);
        CurrentPlayersInfo playerInfo = getPlayerInfo(gameRoom, userId);
        List<InvestmentDetail> investments = getInvestmentDetails(gameRoomId, userId);

        return MyInvestmentResponse.from(playerInfo, investments);
    }

}
