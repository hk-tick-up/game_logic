package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.Request.GameStateUpdateRequest;

import java.time.LocalDateTime;

public interface GameRoomService {
    // 턴 변경
//    TurnUpdateResponse updateTurn(Long gameRoomId);
//    TurnUpdateResponse sendInitialGameState(Long gameRoomId);
    void initializeCache();
    void handleGameStateUpdate(GameStateUpdateRequest request);
    void processNextTurn(Long gameRoomId);
    void updatePlayerCount(Long gameRoomId, int count);
    void cleanupGameRoom(Long gameRoomId);
    void sendTurnData(Long gameRoomId, int turn, LocalDateTime turnEndTime);

}
