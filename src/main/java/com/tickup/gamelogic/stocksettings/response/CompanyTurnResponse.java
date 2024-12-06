package com.tickup.gamelogic.stocksettings.response;

import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import com.tickup.gamelogic.stocksettings.domain.StockData;

public record CompanyTurnResponse(
        String ticker,
        long stockPrice,
        double changeRate,
        String eventContent
) {
    public static CompanyTurnResponse from(StockData stockData, GameEvents gameEvents) {
        return new CompanyTurnResponse(
            stockData.getTicker(),
            stockData.getStockPrice(),
            stockData.getChangeRate(),
            gameEvents.getEventContents()
        );
    }
}
