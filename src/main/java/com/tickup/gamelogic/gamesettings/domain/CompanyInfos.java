package com.tickup.gamelogic.gamesettings.domain;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Class name: CompanyInfos
 * Summary: CompanyInfos JPA entity class
 * Date: 2024.11.20
 * Write by: 양예현
 */
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyInfos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long companyInfosId;

    @Column(nullable = false, unique = true)
    private String ticker;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String industry;

    @ManyToOne
    @JoinColumn(nullable = false, name = "game_rooms_id", referencedColumnName = "gameRoomsId")
    private GameRooms gameRoomsId;
}
