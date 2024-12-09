package com.tickup.gamelogic.stocksettings.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.stocksettings.response.TurnStockDataResponse;

import java.util.List;

public interface StockSettingsService {
    List<String> getGameRoomTickers(Long gameRoomId);
    TurnStockDataResponse getStockDataForTurn(Long gameRoomId, int currentTurn);
    void sendStockUpdate(Long gameRoomId, int currentTurn);
}
