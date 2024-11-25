package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.Request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.Response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;

public interface GameRoomsService {
    // 방 생성 service
    InitGameRoomResponse initGameRoom(InitGameRoomRequest initGameRoomRequest);

    // 게임 방에 참여한 유저 정보 초기화
    //    void initPlayersInfo(String roomId, List<String> players);
}
