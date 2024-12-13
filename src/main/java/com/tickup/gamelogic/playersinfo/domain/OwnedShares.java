package com.tickup.gamelogic.playersinfo.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnedShares {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownedSharesId;

    @Column(nullable = false)
    private String userId; // 플레이어 ID

    @Column(nullable = false)
    private String ticker; // 주식 티커

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private int shares; // 총 보유 주식량

    @Column(nullable = false)
    private int averagePurchasePrice; // 평균 매수가 (수익률 계산용)

    @Column
    private double rateReturn; // 수익률

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;

    // 주식량 증가 메서드
    public void addShares(int sharesToAdd) {
        this.shares += sharesToAdd;
    }

    // 주식량 감소 메서드
    public void decreaseShares(int sharesToSell) {
        if (this.shares < sharesToSell) {
            throw new RuntimeException("보유 주식량을 초과하여 판매할 수 없습니다.");
        }
        this.shares -= sharesToSell;
    }

    // 평균 매수가 갱신 (매수 시 호출)
    public void updateAveragePurchasePrice(int newShares, int newPrice) {
        if (this.shares == 0) {
            // 첫 구매인 경우
            this.averagePurchasePrice = newPrice;
        } else {
            // 추가 구매인 경우 가중 평균 계산
            int currentTotalValue = this.averagePurchasePrice * this.shares;
            int newTotalValue = newPrice * newShares;
            this.averagePurchasePrice = (currentTotalValue + newTotalValue) / (this.shares + newShares);
        }
        // 수익률 업데이트
        updateRateReturn(newPrice);
    }

    // 주식 수익률 계산
    public void updateRateReturn(int currentPrice) {
        if (this.averagePurchasePrice > 0) {
            this.rateReturn = (((double)currentPrice - averagePurchasePrice) * 100.0) / averagePurchasePrice;
        }
    }
}
