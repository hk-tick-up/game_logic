package com.tickup.gamelogic.playersinfo.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.*;

/*
 * Class name: CurrentPlayersInfo
 * Summary: CurrentPlayersInfo JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentPlayersInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long currentPlayersInfoId;

    @Column(nullable = false)
    private String userId;

    @Column
    private int balance; // 현재 보유한 금액

    @Column
    private int netAssets; // 순 자산

    @Column(nullable = false)
    private double returnRate = 0; // 수익률

    @Column(nullable = false)
    private int valuationAmount = 0; // 평가 금액

    @Column(nullable = false)
    private int currentRank; // 순 자산 기반 랭킹

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;

}
