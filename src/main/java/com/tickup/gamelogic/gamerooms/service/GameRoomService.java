package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.Response.TurnUpdateResponse;

public interface GameRoomService {
    // 턴 변경
    TurnUpdateResponse updateTurn(Long gameRoomId);
    void sendInitialGameState(Long gameRoomId);
}
