package com.tickup.gamelogic.gamerooms.Response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;

public record InitGameRoomResponse(
        Long gameRoomsId
) {
    public static InitGameRoomResponse from (GameRooms room) {
        return new InitGameRoomResponse(room.getGameRoomsId());
    }
}
