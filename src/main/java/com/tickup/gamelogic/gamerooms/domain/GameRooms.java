package com.tickup.gamelogic.gamerooms.domain;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameRoomsId;

    @Column
    private int currentTurn;

    @Column
    private Instant currentTurnStartTime;

    @Column
    private int remainingTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrentGameState currentGameState;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rules_id")
    private GameRules gameRules;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @OneToMany(mappedBy = "gameRooms", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CurrentPlayersInfo> currentPlayersInfos = new ArrayList<>();
    public void addCurrentPlayersInfo(CurrentPlayersInfo currentPlayersInfo) {
        currentPlayersInfos.add(currentPlayersInfo);
    }
}

