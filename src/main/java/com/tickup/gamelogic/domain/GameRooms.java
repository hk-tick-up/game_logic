package com.tickup.gamelogic.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

}

