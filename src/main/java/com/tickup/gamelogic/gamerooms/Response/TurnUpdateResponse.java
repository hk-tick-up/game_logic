package com.tickup.gamelogic.gamerooms.Response;

import java.time.Instant;
import java.time.LocalDateTime;

public record TurnUpdateResponse(
     int currentTurn,
     int totalTurn,
     int remainingTime,
     LocalDateTime currentTurnStartTime
) {
    public static TurnUpdateResponse from(int currentTurn, int totalTurn, int remainingTime, LocalDateTime currentTurnStartTime) {
        return new TurnUpdateResponse(currentTurn, totalTurn, remainingTime, currentTurnStartTime);
    }
}
