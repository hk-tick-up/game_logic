package com.tickup.gamelogic.playersinfo.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: PlayerStatus
 * Summary: PlayerStatus JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerStatusId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private boolean status = false;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;

}
