package com.tickup.gamelogic.gamerooms.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: GameRules
 * Summary: GameRules JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameRulesId;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int totalTurns;

    @Column(nullable = false)
    private int remainingTime;

    @Column(nullable = false)
    private int initSeedMoney;

    @Column(nullable = false)
    private int numOfCompanies;
}
