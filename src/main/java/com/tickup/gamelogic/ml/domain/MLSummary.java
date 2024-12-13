package com.tickup.gamelogic.ml.domain;

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
@Table(name = "Summary_tb")
public class MLSummary {
    @Id
    @Column(name = "event_id")
    private int eventId;

    @Column(nullable = false)
    private String summary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "event_id", insertable = false, updatable = false)
    private MLStockEvent stockEvent;
}
