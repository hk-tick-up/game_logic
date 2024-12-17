package com.tickup.gamelogic.gamerooms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tickup.gamelogic.gamerooms.request.GameStateUpdateRequest;
import com.tickup.gamelogic.gamerooms.response.GameEndResponse;
import com.tickup.gamelogic.gamerooms.response.GameStateUpdateResponse;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.gamerooms.response.RankingResponse;
import com.tickup.gamelogic.infrastructure.ReportApiClient;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.playersinfo.request.GameReportRequest;
import com.tickup.gamelogic.playersinfo.response.ReportResponse;
import com.tickup.gamelogic.playersinfo.service.GameReportServiceImpl;
import com.tickup.gamelogic.playersinfo.service.RankingServiceImpl;
import com.tickup.gamelogic.playersinfo.service.TradeServiceImpl;
import com.tickup.gamelogic.stocksettings.service.StockSettingsServiceImpl;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameRoomServiceImpl implements GameRoomService, ApplicationListener<ContextRefreshedEvent> {
    private final GameRoomsRepository gameRoomsRepository;
    private final StockSettingsServiceImpl stockSettingsService;
    private final TradeServiceImpl tradeService;
    private final RankingServiceImpl rankingService;
    private final GameReportServiceImpl gameReportService;

    private final SimpMessagingTemplate messagingTemplate;

    private final ReportApiClient reportApiClient;

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
        log.info("Current turn: {}, Total turn: {}", gameRoom.getCurrentTurn(), gameRoom.getTotalTurn());

        if (nextTurn > gameRoom.getTotalTurn()) {
            processGameEnd(gameRoom);
            return;
        }

        LocalDateTime nextTurnEndTime = LocalDateTime.now().plusSeconds(gameRoom.getRemainingTime());

        // 게임룸 업데이트
        gameRoom.updateTurn(nextTurn, nextTurnEndTime);
        gameRoomsRepository.save(gameRoom);

        // 턴 종료 확인 캐시 초기화
        turnEndConfirmations.get(gameRoomId).clear();

        // 턴 정보 및 주식 데이터 전송
        sendTurnData(gameRoomId, nextTurn, nextTurnEndTime);

        // 모든 플레이어의 투자 정보 업데이트 전송
        tradeService.sendTurnInvestmentUpdates(gameRoom);

        // 랭킹 업데이트
        rankingService.updateRankings(gameRoom);
        sendRankingUpdate(gameRoom);

    }

    @Override
    public void sendTurnData(Long gameRoomId, int turn, LocalDateTime turnEndTime) {
        // 턴 정보 브로드캐스트
        GameStateUpdateResponse response = GameStateUpdateResponse.from(gameRoomId, turn, turnEndTime);
        messagingTemplate.convertAndSend(
                "/topic/gameRoom/" + gameRoomId + "/turnChange",
                response
        );

        // 주식 데이터 브로드캐스트
        stockSettingsService.sendStockUpdate(gameRoomId, turn);

        log.info("Data sent for game room {}: Turn {}", gameRoomId, turn);
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

    @Override
    public void sendRankingUpdate(GameRooms gameRoom) {
        List<RankingResponse> rankings
                = gameRoom.getCurrentPlayersInfos().stream()
                .map(RankingResponse::from)
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend(
                "/topic/gameRoom/" + gameRoom.getGameRoomsId() + "/rankings",
                rankings
        );

        log.info("Sending ranking update for gameRoom {}: {} players",
                gameRoom.getGameRoomsId(), rankings.size());
    }

    private void processGameEnd(GameRooms gameRoom) {
        log.info("Processing game end for game room: {}", gameRoom.getGameRoomsId());

        // 각 플레이어의 리포트 생성 및 전송
        for (CurrentPlayersInfo player : gameRoom.getCurrentPlayersInfos()) {
            try {
                List<GameReportRequest> report = gameReportService.createGameReport(gameRoom, player.getUserId());
//                ReportResponse response = reportApiClient.sendTradeLog(report, player.getUserId(), gameRoom.getGameRoomsId());

                // 클러스터 ID 판별 및 저장
                reportApiClient.processTradeLogAndSaveCluster(report, player.getUserId(), gameRoom.getGameRoomsId());

                log.info("Successfully processed trade log and saved cluster for player: {}", player.getUserId());

            } catch (Exception e) {
                log.error("Failed to process game end report for player: {}", player.getUserId(), e);
                // 실패 처리 (예: 재시도 로직 또는 에러 알림)
            }
        }

        // 게임 상태 업데이트
        gameRoom.endGame();

        // 게임 종료 알림
        sendGameEndNotification(gameRoom);

        // 게임방 캐시 정리
        cleanupGameRoom(gameRoom.getGameRoomsId());
    }

    private void handleReportResponse(GameRooms gameRoom, CurrentPlayersInfo player, ReportResponse response) {
        messagingTemplate.convertAndSend(
                "/topic/gameRoom/" + gameRoom.getGameRoomsId() + "/player/" + player.getUserId() + "/report",
                response
        );
        log.info("Sent report response to player: {}", player.getUserId());
    }

    private void sendGameEndNotification(GameRooms gameRoom) {
        GameEndResponse notification = GameEndResponse.from(gameRoom);
        messagingTemplate.convertAndSend(
                "/topic/gameRoom/" + gameRoom.getGameRoomsId() + "/gameEnd",
                notification
        );
        log.info("Sent game end notification for game room: {}", gameRoom.getGameRoomsId());
    }
}
