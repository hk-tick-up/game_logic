package com.tickup.gamelogic.playersinfo.request;

import com.tickup.gamelogic.playersinfo.domain.TradeLog;
import com.tickup.gamelogic.playersinfo.domain.TradeType;

public record GameReportRequest(
        String USER_ID,
        String GAME_ID,
        int TURN,
        String COMPANY_CODE,
        String ACTION,
        int QUANTITY,
        int PRICE,
        int TOTAL_COST,
        int TOTAL_EARNED,
        int REMAINING_FUNDS,
        String REMAINING_STOCKS
) {
    public static GameReportRequest from(TradeLog log, String gameId, String remainingStocks) {
        String action = log.getTradeType() == TradeType.BUY ? "매수" : "매도";
        int totalCost = log.getTradeType() == TradeType.BUY ? log.getPrice() * log.getShares() : 0;
        int totalEarned = log.getTradeType() == TradeType.SELL ? log.getPrice() * log.getShares() : 0;

        return new GameReportRequest(
                log.getUserId(),
                gameId,
                log.getTurn(),
                log.getCompanyName(),
                action,
                log.getShares(),
                log.getPrice(),
                totalCost,
                totalEarned,
                log.getRemainingFunds(),
                remainingStocks
        );
    }
}
