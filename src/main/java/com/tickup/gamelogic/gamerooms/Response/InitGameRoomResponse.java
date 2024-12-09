package com.tickup.gamelogic.gamerooms.Response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;

import java.time.LocalDateTime;

public record InitGameRoomResponse(
        Long gameRoomsId,
        int totalTurns,
        int currentTurn,
        LocalDateTime currentEndTime
) {
    public static InitGameRoomResponse from (GameRooms gameRoom) {
        return new InitGameRoomResponse(
                gameRoom.getGameRoomsId(),
                gameRoom.getTotalTurn(),
                gameRoom.getCurrentTurn(),
                gameRoom.getCurrentTurnEndTime());
    }
}
