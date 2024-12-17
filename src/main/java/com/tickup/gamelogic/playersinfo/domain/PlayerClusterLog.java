package com.tickup.gamelogic.playersinfo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerClusterLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerClusterLogId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long gameRoomId;

    @Column(nullable = false)
    private Date gameDate;

    @Column(nullable = false)
    private int cluster;
}
