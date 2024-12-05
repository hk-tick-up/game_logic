package com.tickup.gamelogic.gamerooms.Request;

import java.time.LocalDateTime;

public record GameStateUpdateRequest(
        Long gameRoomId,
        int currentTurn,
        String playerId,
        LocalDateTime clientTime
) {
}
