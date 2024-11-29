package com.tickup.gamelogic.gamerooms.Response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.domain.GameRules;

import java.time.LocalDateTime;

public record InitGameProcessResponse(
       int currentTurn,
       int totalTurns,
       int remainingTime,
       LocalDateTime turnStartTime

) {
    public static InitGameProcessResponse from (GameRooms gameRooms) {
        return new InitGameProcessResponse(
                gameRooms.getCurrentTurn(),
                gameRooms.getTotalTurn(),
                gameRooms.getRemainingTime(),
                gameRooms.getCurrentTurnStartTime());

    }
}
