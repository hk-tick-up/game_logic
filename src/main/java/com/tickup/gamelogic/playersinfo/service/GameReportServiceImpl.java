package com.tickup.gamelogic.playersinfo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.playersinfo.domain.TradeLog;
import com.tickup.gamelogic.playersinfo.domain.TradeType;
import com.tickup.gamelogic.playersinfo.repository.TradeLogRepository;
import com.tickup.gamelogic.playersinfo.request.GameReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameReportServiceImpl implements GameReportService {
    private final TradeLogRepository tradeLogRepository;

    @Override
    public List<GameReportRequest> createGameReport(GameRooms gameRoom, String userId) {
        List<TradeLog> tradeLogs
                = tradeLogRepository.findAllByGameRoomsAndUserIdOrderByTurnAsc(gameRoom, userId);

        List<GameReportRequest> reportRequests = new ArrayList<>();
        Map<String, Integer> stockHoldings = new HashMap<>();

        for (TradeLog tradeLog : tradeLogs) {
            if (tradeLog.getShares() > 0) {
                updateStockHoldings(stockHoldings, tradeLog);
                String remainingStocks = calculateRemainingStocks(stockHoldings);

                reportRequests.add(GameReportRequest.from(
                        tradeLog,
                        gameRoom.getGameRoomsId().toString(),
                        remainingStocks
                ));
            }
        }

        return reportRequests;
    }


    /**
     * 턴 별 주식 보유 현황 계산
     */
    private String calculateRemainingStocks(Map<String, Integer> holdings) {
        Map<String, Integer> currentHoldings = new HashMap<>(holdings);
        currentHoldings.values().removeIf(quantity -> quantity <= 0);

        try {
            return new ObjectMapper().writeValueAsString(currentHoldings);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize remaining stocks", e);
            return "{}";
        }
    }


    private void updateStockHoldings(Map<String, Integer> holdings, TradeLog log) {
        int currentHolding = holdings.getOrDefault(log.getCompanyName(), 0);

        if (log.getTradeType() == TradeType.BUY) {
            holdings.put(log.getCompanyName(), currentHolding + log.getShares());
        } else if (log.getTradeType() == TradeType.SELL) {
            holdings.put(log.getCompanyName(), currentHolding - log.getShares());
        }
    }

}
