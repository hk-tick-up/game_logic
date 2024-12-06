package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.stocksettings.domain.CompanyInfo;
import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import com.tickup.gamelogic.stocksettings.repository.CompanyInfoRepository;
import com.tickup.gamelogic.stocksettings.repository.StockDataRepository;
import com.tickup.gamelogic.stocksettings.response.CompanyTurnResponse;
import com.tickup.gamelogic.stocksettings.response.TurnStockDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockSettingsServiceImpl implements StockSettingsService {

    private final CompanyInfoRepository companyInfoRepository;
    private final GameRoomsRepository gameRoomsRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 게임룸별 ticker 리스트 캐시
    private final ConcurrentHashMap<Long, List<String>> gameRoomTickers = new ConcurrentHashMap<>();
    private final StockDataRepository stockDataRepository;

    public List<String> getGameRoomTickers(Long gameRoomId) {
        return gameRoomTickers.computeIfAbsent(gameRoomId,
                id -> gameRoomsRepository.findTickersByGameRoomsId(id));
    }

    public TurnStockDataResponse getStockDataForTurn(Long gameRoomId, int currentTurn) {
        List<String> tickers = getGameRoomTickers(gameRoomId);

        Map<String, StockData> stockDataMap = stockDataRepository
                .findByGameRoomsIdAndTickerAndTurn(gameRoomId, currentTurn, tickers)
                .stream()
                .collect(Collectors.toMap(
                        StockData::getTicker,
                        stockData -> stockData
                ));

        Map<String, GameEvents> gameEventsMap = gameRoomsRepository
                .
    }


}
