package com.tickup.gamelogic.gamerooms.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: GameResultRewards
 * Summary: GameResultRewards JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResultRewards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameResultRewardsId;

    @Column(nullable = false)
    private int playerRank;

    @Column
    private int points;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rules_id", referencedColumnName = "gameRulesId")
    private GameRules gameRulesId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

}
