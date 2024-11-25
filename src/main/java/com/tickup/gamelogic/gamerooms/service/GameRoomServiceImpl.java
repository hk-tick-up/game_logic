package com.tickup.gamelogic.gamerooms.service;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.gamerooms.repository.GameRoomsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {
    private final GameRoomsRepository gameRoomsRepository;
    private final SimpMessageTemplete messagingTemplete;

    @Override
    public void updateTurn(Long gameRoomId) {
        GameRooms gameRooms = gameRoomsRepository.findById(gameRoomId)
                .orElseThrow(() -> new RuntimeException("Game Room not found"));

        // 현재 턴 업데이트 및 제한 시간 초기화
        int nextTurn = gameRooms.getCurrentTurn() + 1;
        Instant nextTurnStartTime = Instant.now();
        int remainingTime = gameRooms.getGameRules().getRemainingTime();

        gameRoomsRepository.updateGameRoomsState(gameRoomId, nextTurn, nextTurnStartTime, remainingTime);

        // 웹소켓으로 클라이언트에게 다음 턴 정보 전송
        TurnUpdateMessage turnUpdateMessage = new TurnUpdateMessage(nextTurn, remainingTime, nextTurnStartTime);
        messagingTemplete.convertAndSend('/topic/game-room' + gameRoomId, turnUpdateMessage)


    }
}
