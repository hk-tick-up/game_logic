package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.response.GameEndDataResponse;

public interface GameEndService {
    GameEndDataResponse sendGameEndData(Long gameRoomId, String userId);
}
