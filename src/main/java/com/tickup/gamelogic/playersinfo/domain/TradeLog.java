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
public class TradeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeLogId;

    @Column(nullable = false)
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String companyName; // 주식 이름

    @Column(nullable = false)
    private int shares; // 거래된 주식 수량

    @Column(nullable = false)
    private int price; // 거래된 가격 (매수/매도 시점의 가격)

    @Column(nullable = false)
    private int remainingFunds; // 그 당시 남은 가격

    @Column(nullable = false)
    private int turn; // 거래가 발생한 턴

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType; // 매수/매도 구분 (BUY/SELL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;
}
