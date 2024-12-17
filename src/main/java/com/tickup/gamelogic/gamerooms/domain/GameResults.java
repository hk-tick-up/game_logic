package com.tickup.gamelogic.gamerooms.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * Class name: GameResults
 * Summary: GameResults JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameResultsId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private int finalRank;

    @Column(nullable = false)
    private int finalReturn;

    @Column(nullable = false)
    private Date endTime;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

}
