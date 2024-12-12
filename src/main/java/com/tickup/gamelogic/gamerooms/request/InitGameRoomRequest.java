package com.tickup.gamelogic.gamerooms.request;

import java.util.List;

public record InitGameRoomRequest(
        String gameType,
        List<String> players // nickname도 추가
) {

}
