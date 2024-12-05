package com.tickup.gamelogic.gamerooms.Response;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.domain.GameRules;

import java.time.LocalDateTime;

public record InitGameProcessResponse(
       int currentTurn,
       int totalTurns,
       LocalDateTime now,
       LocalDateTime currentTurnEndTime

) {
    public static InitGameProcessResponse from (GameRooms gameRooms) {
        return new InitGameProcessResponse(
                gameRooms.getCurrentTurn(),
                gameRooms.getTotalTurn(),
                LocalDateTime.now(),
                gameRooms.getCurrentTurnEndTime());

    }
}
