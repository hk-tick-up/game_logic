package com.tickup.gamelogic.gamerooms.response;

import java.time.LocalDateTime;

public record GameStateUpdateResponse(
        Long gameRoomId,
        int nextTurn,
        LocalDateTime nextTurnEndTime
        //List<String> Plyaers // user 쪽 msa와 통합하면 추가 예정
) {
    public static GameStateUpdateResponse from (Long gameRoomId, int nextTurn, LocalDateTime nextTurnEndTime) {
        return new GameStateUpdateResponse(gameRoomId, nextTurn, nextTurnEndTime);

    }
}
