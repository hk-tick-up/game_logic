package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.ml.repository.MLCorporationRepository;
import com.tickup.gamelogic.ml.repository.MLStockEventRepository;
import com.tickup.gamelogic.ml.repository.MLSummaryRepository;
import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import com.tickup.gamelogic.stocksettings.repository.CompanyInfoRepository;
import com.tickup.gamelogic.stocksettings.repository.StockDataRepository;
import com.tickup.gamelogic.stocksettings.response.CompanyTurnResponse;
import com.tickup.gamelogic.stocksettings.response.TurnStockDataResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
    public void setGameScenario(Long gameRoomId, int currentTurn) {
        System.out.println("---------------setGameScenario---------------");
        System.out.println("GAMEROOM ID: "+gameRoomId);
        /*
            1. 기업 n개 선택
            get n corps from table Corporation_tb
            corp_ticker VARCHAR(10)
            2. 날짜 임의 생성 (날짜 생성 기준?)
            3. 매턴
            기업별 이벤트 택1
            호재/악재 + 변화수준 -> 주가 변동값 설정
            get distinct event_id by corp_ticker from table StockEvent_tb
            when corp_ticker = $current_corp_ticker VARCHAR(10)
            event_id INT, `change` DECIMAL(10,6),
        */
        List<String> allCorpTickers = mlCorporationRepository.findAllCorpTickers();
        // allCorpTickers로부터 n개 기업 선택
        // CompanyInfo에 저장

        // 날짜 임의 생성
        // event_id 1~301 중 임의로 하나 선택, 날짜만 추출

        // 매턴 기업별 이벤트 택1
        // StockData에 전체 시나리오 저장
    }


}
