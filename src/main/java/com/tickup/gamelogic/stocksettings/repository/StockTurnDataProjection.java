package com.tickup.gamelogic.stocksettings.repository;

public interface StockTurnDataProjection {
    String getTicker();
    long getStockPrice();
    double getChangeRate();
    String getEventContents();
}
