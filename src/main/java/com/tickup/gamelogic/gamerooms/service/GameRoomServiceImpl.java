package com.tickup.gamelogic.gamerooms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tickup.gamelogic.gamerooms.Request.GameStateUpdateRequest;
import com.tickup.gamelogic.gamerooms.Response.GameStateUpdateResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameRoomServiceImpl implements GameRoomService, ApplicationListener<ContextRefreshedEvent> {
    private final GameRoomsRepository gameRoomsRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int SYNC_INTERVAL = 1000; // 1초

    // 게임방별 턴 종료 확인을 위한 캐시
    private final ConcurrentHashMap<Long, Set<String>> turnEndConfirmations = new ConcurrentHashMap<>();

    // 게임방별 플레이어 수 캐시
    private final ConcurrentHashMap<Long, Integer> roomPlayerCounts = new ConcurrentHashMap<>();

    // ObjectMapper 설정
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // JavaTimeModule 등록
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용

    // 컨텍스트 초기화 후 캐시 초기화
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeCache();
    }

    // 서버 시작 시 캐시 초기화
    @Override
//    @PostConstruct
    @Transactional
    public void initializeCache() {
        log.info("Initializing cache...");
        List<GameRooms> gameRooms = gameRoomsRepository.findAll();

        if (gameRooms.isEmpty()) {
            log.warn("No game rooms found in the database during cache initialization.");
            return;
        }

        gameRooms.forEach(gameRoom -> {
            int playerCount = gameRoom.getCurrentPlayersInfos().size();
            roomPlayerCounts.put(gameRoom.getGameRoomsId(), playerCount);
            turnEndConfirmations.put(gameRoom.getGameRoomsId(), ConcurrentHashMap.newKeySet());
            log.info("Initialized game room {}: {} players", gameRoom.getGameRoomsId(), playerCount);
        });

        log.info("Cache initialization complete");
    }

    @Override
    @Transactional
    public void handleGameStateUpdate(GameStateUpdateRequest request) {
        Long gameRoomId = request.gameRoomId();
        String playerId = request.playerId();

        Set<String> confirmations = turnEndConfirmations.computeIfAbsent(gameRoomId,
                k -> ConcurrentHashMap.newKeySet());

        // 플레이어의 턴 종료 확인 추가
        confirmations.add(playerId);

        // 현재 게임방의 전체 플레이어 수 확인
        int totalPlayers = roomPlayerCounts.getOrDefault(gameRoomId, 0);

        log.info("Turn end confirmation received - Room: {}, Player: {}, Confirmations: {}/{}",
                gameRoomId, playerId, confirmations.size(), totalPlayers);

        // 모든 플레이어가 턴 종료를 확인했으면 다음 턴으로 진행
        if (confirmations.size() >= totalPlayers) {
            processNextTurn(gameRoomId);
        }
    }

    @Override
    @Transactional
    public void processNextTurn(Long gameRoomId) {
        GameRooms gameRoom = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalStateException("Game room not found: " + gameRoomId));

        // 다음 턴 정보 계산
        int nextTurn = gameRoom.getCurrentTurn() + 1;
        LocalDateTime nextTurnEndTime = LocalDateTime.now().plusMinutes(5);

        // 게임룸 업데이트
        gameRoom.updateTurn(nextTurn, nextTurnEndTime);
        gameRoomsRepository.save(gameRoom);

        // 턴 종료 확인 캐시 초기화
        turnEndConfirmations.get(gameRoomId).clear();

        // 새로운 턴 정보 브로드캐스트
        GameStateUpdateResponse response = GameStateUpdateResponse.from(gameRoomId, nextTurn, nextTurnEndTime);

        messagingTemplate.convertAndSend(
                "/topic/gameRoom/" + gameRoomId + "/turnChange",
                response
        );

        log.info("Turn advanced for game room {}: Turn {}", gameRoomId, nextTurn);
    }

    @Override
    // 플레이어 수 업데이트 (플레이어 입장/퇴장 시 호출)
    public void updatePlayerCount(Long gameRoomId, int count) {
        roomPlayerCounts.put(gameRoomId, count);
        log.info("Player count updated for room {}: {}", gameRoomId, count);
    }

    @Override
    // 게임방 종료 시 캐시 정리
    public void cleanupGameRoom(Long gameRoomId) {
        turnEndConfirmations.remove(gameRoomId);
        roomPlayerCounts.remove(gameRoomId);
        log.info("Cleaned up cache for game room: {}", gameRoomId);
    }

}
