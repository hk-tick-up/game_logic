package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.response.InitGameProcessResponse;
import com.tickup.gamelogic.gamerooms.response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.domain.CurrentGameState;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.domain.GameRules;
import com.tickup.gamelogic.gamerooms.domain.GameType;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.gamerooms.repository.GameRulesRepository;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.stocksettings.service.StockSettingsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InitGameRoomsServiceImpl implements InitGameRoomsService {

    private final GameRoomsRepository gameRoomRepository;
    private final GameRulesRepository gameRulesRepository;
    private final StockSettingsServiceImpl stockSettingsService;
    private final GameRoomServiceImpl gameRoomService;

    /*
     * Class name: initGameRoom
     * Summary: 게임 방 생성 시
     *          1. 게임방 정보 초기화
     *          2. 플레이어 정보 초기화
     * Date: 2024.11.22
     * Write by: 양예현
     */
    @Override
    public InitGameRoomResponse initGameRoom(InitGameRoomRequest request) {
        try {
            log.info("Starting game room initialization with request: {}", request);

            GameRules gameRules = gameRulesRepository.findGameRulesByGameType(
                    GameType.valueOf(request.gameType().toUpperCase())
            );
            log.info("Found game rules with ID: {}", gameRules.getGameRulesId());

            // 방 정보 초기화
            GameRooms gameRoom = GameRooms.builder()
                    .currentTurn(1)
                    .totalTurn(gameRules.getTotalTurns())
                    .currentTurnStartTime(LocalDateTime.now())
                    .currentTurnEndTime(LocalDateTime.now().plusSeconds(gameRules.getRemainingTime()))
                    .remainingTime(gameRules.getRemainingTime())
                    .currentGameState(CurrentGameState.MOVING_ON)
                    .gameRules(gameRules)
                    .gameType(GameType.valueOf(request.gameType().toUpperCase()))
                    .build();

            log.info("Created game room object with initial settings");

            // 게임 방에 있는 플레이어 정보 초기화
            request.players().forEach(player -> {
                try {
                    CurrentPlayersInfo currentPlayersInfo = CurrentPlayersInfo.builder()
                            .userId(player)
                            .balance(gameRules.getInitSeedMoney())
                            .netAssets(gameRules.getInitSeedMoney())
                            .returnRate(0)
                            .initialAssets(gameRules.getInitSeedMoney())
                            .valuationAmount(0)
                            .currentRank(null)
                            .isTurnEnd(false)
                            .gameRooms(gameRoom)
                            .build();
                    gameRoom.addCurrentPlayersInfo(currentPlayersInfo);
                    log.info("Added player info for user: {}", player);
                } catch (Exception e) {
                    log.error("Error adding player info for user: {}", player, e);
                    throw new RuntimeException("Failed to add player info", e);
                }
            });

            GameRooms newGameRoom = gameRoomRepository.save(gameRoom);
            log.info("Successfully saved game room with ID: {}", newGameRoom.getGameRoomsId());

            // 게임 시나리오 설정을 여기서 바로 실행
            log.info("Setting up game scenario for room ID: {}", newGameRoom.getGameRoomsId());
            stockSettingsService.setGameScenario(newGameRoom.getGameRoomsId(), newGameRoom.getCurrentTurn());
            stockSettingsService.sendStockUpdate(newGameRoom.getGameRoomsId(), newGameRoom.getCurrentTurn());

            return InitGameRoomResponse.from(newGameRoom);
        } catch (Exception e) {
            log.error("Error during game room initialization", e);
            throw new RuntimeException("Failed to initialize game room", e);
        }
    }

    @Override
    @Transactional
    public InitGameProcessResponse initGameProcess(Long gameRoomId) {
        try {
            log.info("Starting game process initialization for room ID: {}", gameRoomId);

            GameRooms gameRooms = gameRoomRepository.findById(gameRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("Game room not found: " + gameRoomId));

            log.info("Found game room. Setting game scenario...");


            // 1턴 데이터와 초기화 데이터 WebSocket으로 전송
            log.info("Sending initial stock update...");
            stockSettingsService.sendStockUpdate(gameRoomId, gameRooms.getCurrentTurn());
            log.info("Stock update sent successfully");

            return InitGameProcessResponse.from(gameRooms);
        } catch (Exception e) {
            log.error("Error during game process initialization for room: {}", gameRoomId, e);
            throw new RuntimeException("Failed to initialize game process", e);
        }
    }
}