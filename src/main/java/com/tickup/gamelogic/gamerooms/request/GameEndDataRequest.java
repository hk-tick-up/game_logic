package com.tickup.gamelogic.gamerooms.request;

public record GameEndDataRequest(
        Long gameRoomId,
        String userId
) {
}
