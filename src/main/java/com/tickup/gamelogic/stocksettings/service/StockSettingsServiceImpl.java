package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.ml.domain.MLCorporation;
import com.tickup.gamelogic.ml.domain.MLStockEvent;
import com.tickup.gamelogic.ml.repository.MLCorporationRepository;
import com.tickup.gamelogic.ml.repository.MLStockEventRepository;
import com.tickup.gamelogic.ml.repository.MLSummaryRepository;
import com.tickup.gamelogic.stocksettings.domain.CompanyInfo;
import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import com.tickup.gamelogic.stocksettings.repository.CompanyInfoRepository;
import com.tickup.gamelogic.stocksettings.repository.GameEventsRepository;
import com.tickup.gamelogic.stocksettings.repository.StockDataRepository;
import com.tickup.gamelogic.stocksettings.response.CompanyTurnResponse;
import com.tickup.gamelogic.stocksettings.response.TurnStockDataResponse;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockSettingsServiceImpl implements StockSettingsService {

    private final CompanyInfoRepository companyInfoRepository;
    private final GameRoomsRepository gameRoomsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MLCorporationRepository mlCorporationRepository;
    private final MLStockEventRepository mlStockEventRepository;
    private final MLSummaryRepository mlSummaryRepository;

    // 게임룸별 ticker 리스트 캐시
    private final ConcurrentHashMap<Long, List<String>> gameRoomTickers = new ConcurrentHashMap<>();
    private final StockDataRepository stockDataRepository;
    private final GameEventsRepository gameEventsRepository;

    @Override
    public List<String> getGameRoomTickers(Long gameRoomId) {
        return gameRoomTickers.computeIfAbsent(gameRoomId,
                gameRoomsRepository::findTickersByGameRoomsId);
    }

    @Override
    public TurnStockDataResponse getStockDataForTurn(Long gameRoomId, int currentTurn) {
        List<String> tickers = getGameRoomTickers(gameRoomId);

        // StockData와 GameEvents를 함께 가져오는 쿼리
        List<StockData> stockDataList = stockDataRepository.findTurnDataWithEventsByGameRoomIdAndTurnAndTickersIn(
                gameRoomId, currentTurn, tickers
        );

        // CompanyTurnResponse 생성
        Map<String, CompanyTurnResponse> companyTurnResponses = stockDataList.stream()
                .collect(Collectors.toMap(
                        StockData::getTicker,
                        stockData -> {
                            GameEvents gameEvent = stockData.getGameRooms().getGameEvents().stream()
                                    .filter(event -> event.getTicker().equals(stockData.getTicker()))
                                    .findFirst()
                                    .orElse(null);
                            return CompanyTurnResponse.from(stockData, gameEvent);
                        }
                ));

        return TurnStockDataResponse.from(gameRoomId, currentTurn, companyTurnResponses);
    }

    @Override
    public void sendStockUpdate(Long gameRoomId, int currentTurn) {
        try {
            TurnStockDataResponse stockData = getStockDataForTurn(gameRoomId, currentTurn);
            messagingTemplate.convertAndSend(
                    "/topic/gameRoom/" + gameRoomId + "/stockUpdate",
                    stockData
            );
            log.info("Stock data sent for game room {} turn {}", gameRoomId, currentTurn);
        } catch (Exception e) {
            log.error("Failed to send stock data for game room {} turn {}: {}", gameRoomId, currentTurn, e.getMessage());
            messagingTemplate.convertAndSend(
                    "/topic/gameRoom/" + gameRoomId + "/error",
                    "Failed to load stock data for turn " + currentTurn
            );
        }
    }

    @Override
    @Transactional
    public void setGameScenario(Long gameRoomId, int currentTurn) {
        final int MAX_CORP_COUNT = 4;
        final int MAX_TURN_COUNT = 5;

        // 실제 GameRooms 엔티티를 조회
        GameRooms gameRoom = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found: " + gameRoomId));
        log.info("Setting game scenario for game room: {}", gameRoomId);

        // allCorpTickers로부터 n개 기업 선택
        List<MLCorporation> randomCorps = pickNRandomCorps(MAX_CORP_COUNT);
        log.info("Selected {} corporations for game room {}", randomCorps.size(), gameRoomId);

        // CompanyInfo에 저장
        List<CompanyInfo> savedCompanies = randomCorps.stream().map(corp -> {
            CompanyInfo companyInfo = CompanyInfo.builder()
                    .ticker(corp.getCorpTicker())
                    .companyName(corp.getCorpName())
                    .industry(corp.getCorpIndustry())
                    .gameRooms(gameRoom)  // 실제 엔티티 사용
                    .build();
            CompanyInfo saved = companyInfoRepository.save(companyInfo);
            log.info("Saved company info: {} for game room {}", saved.getTicker(), gameRoomId);
            return saved;
        }).collect(Collectors.toList());

        // 매턴 기업별 이벤트 생성
        savedCompanies.forEach(company -> {
            List<MLStockEvent> stockEvents = pickNRandomEvents(company.getTicker(), MAX_TURN_COUNT);
            int stockPrice = 0;

            for(int turn=1; turn<=MAX_TURN_COUNT; turn++) {
                MLStockEvent currentEvent = stockEvents.get(turn-1);
                double changeRate = getChangeRate(currentEvent);

                if(turn == 1) {
                    stockPrice = currentEvent.getClosedPrice();
                } else {
                    stockPrice = (int) (stockPrice * (1 + changeRate/100));
                }

                StockData stockData = StockData.builder()
                        .ticker(company.getTicker())
                        .companyName(company.getCompanyName())
                        .turn(turn)
                        .targetDate(currentEvent.getEventDate())
                        .stockPrice(stockPrice)
                        .changeRate(changeRate)
                        .gameRooms(gameRoom)  // 실제 엔티티 사용
                        .build();
                StockData savedStockData = stockDataRepository.save(stockData);

                GameEvents gameEvents = GameEvents.builder()
                        .eventContents(mlSummaryRepository.getSummaryById(currentEvent.getEventId()))
                        .stockData(savedStockData)
                        .ticker(savedStockData.getTicker())
                        .turn(turn)
                        .targetDate(savedStockData.getTargetDate())
                        .gameRooms(gameRoom)  // 실제 엔티티 사용
                        .build();
                GameEvents savedGameEvent = gameEventsRepository.save(gameEvents);
            }
        });

        // 캐시 업데이트
        gameRoomTickers.put(gameRoomId,
                savedCompanies.stream()
                        .map(CompanyInfo::getTicker)
                        .collect(Collectors.toList())
        );

        log.info("Completed scenario setup for game room: {}", gameRoomId);
    }
    private double getChangeRate(MLStockEvent currentEvent) {
        boolean sign = currentEvent.getPriceChangeSign() == 1;
        int size = currentEvent.getPriceChangeSize();
        // size: 1, 2, 3
        // 실제 변화율: 0~2%, 2~4%, 4~10%
        double changeRate = 0;
        switch(size){
            case 1:
                changeRate = randomNotZero()*2; // 0~2%
                break;
            case 2:
                changeRate = 2 + randomNotZero()*2; // 2~4%
                break;
            case 3:
                changeRate = 4 + randomNotZero()*6; // 4~10%
                break;
            default:
        }
        return sign ? changeRate : -changeRate;
    }

    private double randomNotZero(){
        double random = Math.random();
        return random == 0 ? 0.000001 : random;
    }

    private List<MLCorporation> pickNRandomCorps(int n) {
        List<MLCorporation> allCorpTickers = mlCorporationRepository.findAll();
        Collections.shuffle(allCorpTickers);
        return allCorpTickers.subList(0, n);
    }
    private List<MLStockEvent> pickNRandomEvents(String ticker, int n) {
        List<MLStockEvent> allEvents = mlStockEventRepository.findAllByCorpTicker(ticker);
        Collections.shuffle(allEvents);
        return allEvents.subList(0, n);
    }


}
