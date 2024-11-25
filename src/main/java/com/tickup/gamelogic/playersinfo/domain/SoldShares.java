package com.tickup.gamelogic.playersinfo.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: SoldShares
 * Summary: SoldShares JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SoldShares {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long soldSharesId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private int shares;

    @Column(nullable = false)
    private int sellPrice;

    @Column(nullable = false)
    private int sellTurn;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRooms;
}
