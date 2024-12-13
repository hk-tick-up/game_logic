package com.tickup.gamelogic.gamerooms.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.stocksettings.domain.CompanyInfo;
import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private int totalTurn;

    @Column
    private int currentTurn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column
    private LocalDateTime currentTurnStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column
    private LocalDateTime currentTurnEndTime;


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

    @OneToMany(mappedBy = "gameRooms", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CurrentPlayersInfo> currentPlayersInfos = new ArrayList<>();
    public void addCurrentPlayersInfo(CurrentPlayersInfo currentPlayersInfo) {
        currentPlayersInfos.add(currentPlayersInfo);
    }

    @OneToMany(mappedBy = "gameRooms", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CompanyInfo> companyInfos = new ArrayList<>();
    public void addCompanyInfo(CompanyInfo companyInfo) {
        companyInfos.add(companyInfo);
    }

    @OneToMany(mappedBy = "gameRooms", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GameEvents> gameEvents = new ArrayList<>();

    public void addGameEvent(GameEvents gameEvent) {
        gameEvents.add(gameEvent);
    }


    public void updateTurn(int nextTurn, LocalDateTime nextTurnEndTime) {
        this.currentTurn = nextTurn;
        this.currentTurnStartTime = LocalDateTime.now();
        this.currentTurnEndTime = nextTurnEndTime;

        // 게임 종료 조건 체크
        if (nextTurn >= this.totalTurn) {
            this.currentGameState = CurrentGameState.GAME_END;
        }
    }

    public void endGame() {
        this.currentGameState = CurrentGameState.GAME_END;
    }
}

