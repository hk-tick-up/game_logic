package com.tickup.gamelogic.gamerooms.Request;

import com.tickup.gamelogic.gamerooms.domain.GameType;

import java.util.List;

public record InitGameRoomRequest(
        String gameType,
        List<String> players
) {

}
