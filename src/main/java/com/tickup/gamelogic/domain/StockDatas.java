package com.tickup.gamelogic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * Class name: StockDatas
 * Summary: StockDatas JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDatas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stockDatasId;

    @Column(nullable = false)
    private int turn;

    @Column(nullable = false)
    private Date targetDate;

    @Column(nullable = false)
    private long stockPrice;

    @Column(nullable = false)
    private String ticker;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;

}
