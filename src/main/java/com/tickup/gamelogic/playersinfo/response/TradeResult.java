package com.tickup.gamelogic.playersinfo.response;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;

import java.util.List;

public record TradeResult(
        CurrentPlayersInfo playerInfo,
        List<InvestmentDetail> investments
) {
}
