package com.tickup.gamelogic.gamerooms.controller;

import com.tickup.gamelogic.config.security.JwtTokenProvider;
import com.tickup.gamelogic.gamerooms.request.GameEndDataRequest;
import com.tickup.gamelogic.gamerooms.request.GameStateUpdateRequest;
import com.tickup.gamelogic.gamerooms.request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.response.GameEndDataResponse;
import com.tickup.gamelogic.gamerooms.response.InitGameProcessResponse;
import com.tickup.gamelogic.gamerooms.response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.gamerooms.service.GameEndServiceImpl;
import com.tickup.gamelogic.gamerooms.service.GameRoomServiceImpl;
import com.tickup.gamelogic.gamerooms.service.InitGameRoomsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/gamelogic")

@Controller
@Slf4j
public class GameRoomsController {

    private final InitGameRoomsServiceImpl initGameRoomsService;
    private final GameRoomServiceImpl gameRoomService;
    private final GameRoomsRepository gameRoomsRepository;
    private final GameEndServiceImpl gameEndService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 게임 방 초기화
     */
    @PostMapping
    public ResponseEntity<InitGameRoomResponse> initGameRoom(@RequestBody InitGameRoomRequest request) {
        log.info(String.valueOf(request));
        InitGameRoomResponse response = initGameRoomsService.initGameRoom(request);

        log.info(String.valueOf(response));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameRoomId}/serverTime")
    public ResponseEntity<InitGameProcessResponse> initGameProcess(@PathVariable("gameRoomId") Long gameRoomId) {
        InitGameProcessResponse response = initGameRoomsService.initGameProcess(gameRoomId);
        return ResponseEntity.ok(response);

    }

    @MessageMapping("/gameRoom/{gameRoomId}/turnEnd")
    public void handleTurnEnd(@Payload GameStateUpdateRequest request,
                              @DestinationVariable Long gameRoomId) {
        log.info("Turn end request received for game room: {}, player: {}",
                gameRoomId, request.playerId());
        gameRoomService.handleGameStateUpdate(request);
    }

    // 플레이어 입장 시 호출
    @MessageMapping("/gameRoom/{gameRoomId}/playerJoin")
    public void handlePlayerJoin(@DestinationVariable Long gameRoomId,
                                 @Payload int playerCount) {
        log.info("Player joined game room: {}, new player count: {}",
                gameRoomId, playerCount);
        gameRoomService.updatePlayerCount(gameRoomId, playerCount);
    }

    // 플레이어 퇴장 시 호출
    @MessageMapping("/gameRoom/{gameRoomId}/playerLeave")
    public void handlePlayerLeave(@DestinationVariable Long gameRoomId,
                                  @Payload int playerCount) {
        log.info("Player left game room: {}, new player count: {}",
                gameRoomId, playerCount);
        gameRoomService.updatePlayerCount(gameRoomId, playerCount);
    }

    // 게임방 종료 시 호출
    @MessageMapping("/gameRoom/{gameRoomId}/end")
    public void handleGameEnd(@DestinationVariable Long gameRoomId) {
        log.info("Game room ended: {}", gameRoomId);
        gameRoomService.cleanupGameRoom(gameRoomId);
    }

    @MessageMapping("/gameRoom/{gameRoomId}/fetchInitialTurnData")
    public void fetchInitialTurnData(@DestinationVariable Long gameRoomId) {
        // 현재 턴 정보 및 주식 데이터를 브로드캐스트
        GameRooms gameRoom = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalStateException("Game room not found: " + gameRoomId));

        int currentTurn = gameRoom.getCurrentTurn();
        LocalDateTime turnEndTime = gameRoom.getCurrentTurnEndTime();

        gameRoomService.sendTurnData(gameRoomId, currentTurn, turnEndTime);
    }

    /**
     * 게임 엔딩 정보 출력 반환
     */
    @PostMapping("/{gameRoomId}/gameEndData")
    public GameEndDataResponse sendGameEndData(@RequestBody GameEndDataRequest request,
                                               @PathVariable("gameRoomId") Long gameRoomId,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        // JWT 토큰 유효성 검증
        String token = authorizationHeader.replace("Bearer ", "");
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid JWT token.");
        }

        // GameEndDataResponse 반환
        return gameEndService.sendGameEndData(gameRoomId, request.userId());

    }

}
