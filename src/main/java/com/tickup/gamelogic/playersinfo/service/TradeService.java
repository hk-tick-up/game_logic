package com.tickup.gamelogic.playersinfo.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.playersinfo.domain.TradeType;
import com.tickup.gamelogic.playersinfo.response.MyInvestmentResponse;
import com.tickup.gamelogic.playersinfo.response.TradeResponse;

public interface TradeService {
    TradeResponse processTrade(Long gameRoomId, String userId, String ticker, int shares, TradeType tradeType);
    void sendTurnInvestmentUpdates(GameRooms gameRoom);
    MyInvestmentResponse getInvestments(Long gameRoomId, String userId);
}

