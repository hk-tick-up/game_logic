package com.tickup.gamelogic.gamerooms.domain;

import jakarta.persistence.*;
import lombok.*;

/*
 * Class name: GameRooms
 * Summary: GameRooms JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRooms {
    @Id
    private String gameRoomsId;

    @Column
    private int currentTurn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrentGameState currentGameState;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rules_id")
    private GameRules gameRulesId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    public enum CurrentGameState {
        ONGOING, //진행 중
        MOVING_ON, //다음 턴으로 넘어가는 중
        GAME_END // 게임 끝
    }

    public enum GameType {
        BASIC,
        CONTEST
    }

}

