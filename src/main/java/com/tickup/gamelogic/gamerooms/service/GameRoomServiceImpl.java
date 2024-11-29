package com.tickup.gamelogic.gamerooms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tickup.gamelogic.gamerooms.Response.TurnUpdateResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomsRepository gameRoomsRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ObjectMapper 설정
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // JavaTimeModule 등록
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용

    @Override
    public TurnUpdateResponse updateTurn(Long gameRoomId) {
        GameRooms gameRooms = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new RuntimeException("Game Room not found"));

        // 현재 턴 업데이트 및 제한 시간 초기화
        int nextTurn = gameRooms.getCurrentTurn() + 1;
        LocalDateTime nextTurnStartTime = LocalDateTime.now();
        int remainingTime = gameRooms.getGameRules().getRemainingTime();
        int totalTurn = gameRooms.getTotalTurn();

        gameRoomsRepository.updateGameRoomsState(gameRoomId, nextTurn, nextTurnStartTime, remainingTime);

        // 웹소켓으로 클라이언트에게 다음 턴 정보 전송
        TurnUpdateResponse turnUpdateResponse = TurnUpdateResponse.from(nextTurn, totalTurn, remainingTime, nextTurnStartTime);
        try {
            // JSON 변환 후 로그 출력
            String jsonResponse = objectMapper.writeValueAsString(turnUpdateResponse);
            log.info("Serialized JSON Response: {}", jsonResponse);

            // WebSocket 메시지 전송
            messagingTemplate.convertAndSend("/topic/game-room/" + gameRoomId + "/game-process", turnUpdateResponse);
        } catch (Exception e) {
            log.error("Error serializing TurnUpdateResponse", e);
        }

        // 로그 추가
//        log.info("WebSocket message sent to {}: {}", destination, turnUpdateResponse);

        return turnUpdateResponse;
    }

    public void sendInitialGameState(Long gameRoomId) {
        // 게임 방 데이터 조회
        GameRooms gameRooms = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new RuntimeException("Game Room not found"));

        // 초기 상태 구성
        TurnUpdateResponse initialResponse = TurnUpdateResponse.from(
                gameRooms.getCurrentTurn(),
                gameRooms.getTotalTurn(),
                gameRooms.getGameRules().getRemainingTime(),
                LocalDateTime.now()
        );

        // WebSocket으로 전송
        messagingTemplate.convertAndSend("/topic/game-room/" + gameRoomId + "/game-process", initialResponse);

        log.info("Initial game state sent: {}", initialResponse);
    }

}
