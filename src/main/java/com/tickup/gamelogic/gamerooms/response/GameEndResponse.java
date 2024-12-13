package com.tickup.gamelogic.gamerooms.response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;

import java.time.LocalDateTime;

public record GameEndResponse(
        Long gameRoomId,
        LocalDateTime endTime
) {
    public static GameEndResponse from(GameRooms gameRoom) {
        return new GameEndResponse(
                gameRoom.getGameRoomsId(),
                LocalDateTime.now()
        );
    }
}
