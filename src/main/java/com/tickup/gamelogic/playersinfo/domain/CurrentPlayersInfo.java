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

    @Column(nullable = false)
    private int balance; // 현재 보유한 금액

    @Column(nullable = false)
    private int netAssets; // 순 자산

    @Column(nullable = false)
    private double returnRate; // 수익률

    @Column
    private int valuationAmount; // 평가 금액

    @Column
    private Integer currentRank; // 순 자산 기반 랭킹

    @Column
    private boolean isTurnEnd; // '턴 마침' 여부 (모든 사용자가 '턴 마침'이면 제한시간 전에 턴 넘어가도록)

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;

}
