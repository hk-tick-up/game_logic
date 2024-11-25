package com.tickup.gamelogic.gamerooms.controller;


import com.tickup.gamelogic.gamerooms.Request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.Response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.service.GameRoomsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gamerooms")
public class GameRoomsController {

    private final GameRoomsServiceImpl gameRoomsService;

    @PostMapping
    public ResponseEntity<InitGameRoomResponse> initGameRoom(@RequestBody InitGameRoomRequest request) {
        // 방 정보 및 유저 정보 초기화
        InitGameRoomResponse response =  gameRoomsService.initGameRoom(request);

        // 성공 응답 반환
        return ResponseEntity.ok(response);
    }

}
