package com.tickup.gamelogic.domain;

/*
 * Enum name: CurrentTurnState
 * Summary: GameRooms의 CurrentTurnState enum
 * Date: 2024.11.20
 * Write by: 양예현
 */
public enum CurrentGameState {
    ONGOING, //진행 중
    MOVING_ON, //다음 턴으로 넘어가는 중
    GAME_END // 게임 끝
}
