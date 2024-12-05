package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.Request.InitGameProcessRequest;
import com.tickup.gamelogic.gamerooms.Request.InitGameRoomRequest;
import com.tickup.gamelogic.gamerooms.Response.InitGameProcessResponse;
import com.tickup.gamelogic.gamerooms.Response.InitGameRoomResponse;
import com.tickup.gamelogic.gamerooms.domain.CurrentGameState;
import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.domain.GameRules;
import com.tickup.gamelogic.gamerooms.domain.GameType;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import com.tickup.gamelogic.gamerooms.repository.GameRulesRepository;
import com.tickup.gamelogic.playersinfo.Repository.CurrentPlayersInfoRepository;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InitGameRoomsServiceImpl implements InitGameRoomsService {

    private final GameRoomsRepository gameRoomRepository;
    private final GameRulesRepository gameRulesRepository;
    private final CurrentPlayersInfoRepository currentPlayersInfoRepository;

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
                    .valuationAmount(0)
                    .currentRank(null)
                    .isTurnEnd(false)
                    .gameRooms(gameRoom)
                    .build();
            gameRoom.addCurrentPlayersInfo(currentPlayersInfo);
        });

       GameRooms newGameRoom = gameRoomRepository.save(gameRoom);

       return InitGameRoomResponse.from(newGameRoom);
    }

    @Override
    public InitGameProcessResponse initGameProcess(Long gameRoomId) {
        GameRooms gameRooms = gameRoomRepository.findById(gameRoomId).orElse(null);

        return InitGameProcessResponse.from(gameRooms);
    }
}
