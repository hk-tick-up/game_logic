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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
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
        GameRules gameRules = gameRulesRepository.findGameRulesByGameType(
                GameType.valueOf(request.gameType().toUpperCase())
        );

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

        // 게임 방에 있는 플레이어 정보 초기화
        request.players().forEach(player -> {
            CurrentPlayersInfo currentPlayersInfo = CurrentPlayersInfo.builder()
                    .userId(player) // -> 여러명이니까 List에 있는만큼 build 반복문으로?
                    .balance(gameRules.getInitSeedMoney())
                    .netAssets(gameRules.getInitSeedMoney()) // 순 자신은 처음에 시드머니와 동일
                    .returnRate(0)
                    .initialAssets(gameRules.getInitSeedMoney()) // 초기 자산은 시드머니와 동일
                    .valuationAmount(0)
                    .currentRank(null)
                    .isTurnEnd(false)
                    .gameRooms(gameRoom)
                    .build();
            gameRoom.addCurrentPlayersInfo(currentPlayersInfo);
        });

        GameRooms newGameRoom = gameRoomRepository.save(gameRoom);

        // 게임 시나리오 설정


       return InitGameRoomResponse.from(newGameRoom);
    }

    @Override
    public InitGameProcessResponse initGameProcess(Long gameRoomId) {
        GameRooms gameRooms = gameRoomRepository.findById(gameRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Game room not found: " + gameRoomId));


        // 게임 시나리오 설정
        stockSettingsService.setGameScenario(gameRoomId, gameRooms.getCurrentTurn());
        // 1턴 데이터와 초기화 데이터 WebSocket으로 전송
        stockSettingsService.sendStockUpdate(gameRoomId, gameRooms.getCurrentTurn());

        return InitGameProcessResponse.from(gameRooms);
    }
}
