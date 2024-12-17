package com.tickup.gamelogic.gamerooms.response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;

import java.time.LocalDateTime;

public record InitGameProcessResponse(
        Long gameRoomId,
       int currentTurn,
       int totalTurns,
       LocalDateTime now,
       LocalDateTime currentTurnEndTime

) {
    public static InitGameProcessResponse from (GameRooms gameRooms) {
        return new InitGameProcessResponse(
                gameRooms.getGameRoomsId(),
                gameRooms.getCurrentTurn(),
                gameRooms.getTotalTurn(),
                LocalDateTime.now(),
                gameRooms.getCurrentTurnEndTime());

    }
}
