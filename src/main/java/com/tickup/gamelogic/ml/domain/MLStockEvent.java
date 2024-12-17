package com.tickup.gamelogic.ml.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "StockEvent_tb")
public class MLStockEvent {
    @Id
    @Column(name = "event_id")
    private int eventId;

    @Column(nullable = false, name = "event_date")
    private Date eventDate;

    @Column(nullable = false, name = "corp_ticker")
    private String corpTicker;

    @Column(nullable = false, name = "`change`")
    private int priceChangeBeforeClosed;

    @Column(nullable = false, name = "close")
    private int closedPrice;

    @Column(nullable = false, name = "volume")
    private int tradeVolume;

    @Column(nullable = false, name = "sign")
    private int priceChangeSign;

    @Column(nullable = false, name = "level")
    private int priceChangeSize;
}
