package com.tickup.gamelogic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: PurchaseShares
 * Summary: PurchaseShares JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseShares {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long purchaseSharesId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private int shares;

    @Column(nullable = false)
    private int purchasePrice;

    @Column(nullable = false)
    private int purchaseTurn;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;
}
