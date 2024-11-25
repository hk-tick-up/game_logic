package com.tickup.gamelogic.gamerooms.controller;


import com.tickup.gamelogic.gamerooms.Request.InitGameProcessRequest;
import com.tickup.gamelogic.gamerooms.Request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.Response.InitGameProcessResponse;
import com.tickup.gamelogic.gamerooms.Response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.service.InitGameRoomsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/gamelogic")
public class GameRoomsController {

    private final InitGameRoomsServiceImpl initGameRoomsService;

    @PostMapping
    public ResponseEntity<InitGameRoomResponse> initGameRoom(@RequestBody InitGameRoomRequest request) {
        // 방 정보 및 유저 정보 초기화
        InitGameRoomResponse response =  initGameRoomsService.initGameRoom(request);

        // 성공 응답 반환
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameRoomId}/init-game-process")
    public ResponseEntity<InitGameProcessResponse> initGameProcess(
            @PathVariable("gameRoomId") Long gameRoomId,
            @RequestBody InitGameProcessRequest request) {
        InitGameProcessResponse response = initGameRoomsService.initGameProcess(request);
        return ResponseEntity.ok(response);
    }

}
