package com.tickup.gamelogic.gamerooms.Response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.domain.GameRules;

public record InitGameProcessResponse(
       int totalTurns,
       int remainingTime
) {
    public static InitGameProcessResponse from (GameRules gameRules) {
        return new InitGameProcessResponse(
                gameRules.getTotalTurns(),
                gameRules.getRemainingTime());
    }
}
