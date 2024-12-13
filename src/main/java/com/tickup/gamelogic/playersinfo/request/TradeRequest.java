package com.tickup.gamelogic.playersinfo.request;

import com.tickup.gamelogic.playersinfo.domain.TradeType;

public record TradeRequest(
        String userId,
        String ticker,
        int shares,
        TradeType tradeType
) {
}

