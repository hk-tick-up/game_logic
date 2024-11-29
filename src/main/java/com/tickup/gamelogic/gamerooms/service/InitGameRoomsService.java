package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.Request.InitGameProcessRequest;
import com.tickup.gamelogic.gamerooms.Request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.Response.InitGameProcessResponse;
import com.tickup.gamelogic.gamerooms.Response.InitGameRoomResponse;

public interface InitGameRoomsService {
    // 방 생성 service
    InitGameRoomResponse initGameRoom(InitGameRoomRequest initGameRoomRequest);

    // 게임 방 생성 시 총 턴 수 & 제한 시간 값 전달
    // InitGameProcessResponse initGameProcess(InitGameProcessRequest initGameProcessRequest);
}
