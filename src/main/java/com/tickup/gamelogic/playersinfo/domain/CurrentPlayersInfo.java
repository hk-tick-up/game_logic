package com.tickup.gamelogic.playersinfo.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Long currentPlayersInfoId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private int balance; // 현재 보유한 금액

    @Column(nullable = false)
    private int netAssets; // 순 자산

    @Column(nullable = false)
    private double returnRate; // 수익률

    @Column(nullable = false)
    private int initialAssets; // 초기 자산 (수익률 계산용)

    @Column
    private int valuationAmount; // 평가 금액

    @Column
    private Integer currentRank; // 순 자산 기반 랭킹

    @Column
    private boolean isTurnEnd; // '턴 마침' 여부 (모든 사용자가 '턴 마침'이면 제한시간 전에 턴 넘어가도록)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;

    // Balance update 메서드
    public void updateBalance(int amount) {
        if (this.balance + amount < 0) {
            throw new RuntimeException("보유 잔액이 부족합니다.");
        }
        this.balance += amount;
    }

    // 평가 금액 update 메서드
    public void updateValuationAmount (List<Integer> stockValues) {
        this.valuationAmount = stockValues.stream().mapToInt(Integer::intValue).sum();
    }

    // 순자산 update 메서드
    public void updateNetAssets() {
        this.netAssets = this.balance + this.valuationAmount;
    }

    // 총 수익률 계산
    public void updateReturnRate() {
        if (this.initialAssets > 0) {
            this.returnRate = ((double) (this.netAssets - this.initialAssets) / this.initialAssets) * 100;
        } else {
            this.returnRate = 0; // 초기 자산이 0인 경우 (예외 처리)
        }
    }

    // 순위 업데이트 메서드
    public void updateRank(int newRank) {
        this.currentRank = newRank;
    }

}
