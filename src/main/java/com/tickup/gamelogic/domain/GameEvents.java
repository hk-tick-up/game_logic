package com.tickup.gamelogic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * Class name: GameEvents
 * Summary: GameEvents JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameEventsId;

    @Column(nullable = false)
    private String eventContents;

    @ManyToOne
    @JoinColumn(nullable = false, name = "stock_datas_id")
    private StockDatas stockDatasId;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private int turn;

    @Column(nullable = false)
    private Date targetDate;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;
}
