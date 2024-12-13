package com.tickup.gamelogic.playersinfo.response;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;

import java.util.List;

public record MyInvestmentResponse (
        int balance,         // 현금 잔액
        int netAssets,         // 순자산 (현금 + 주식 평가 금액)
        int valuationAmount,    // 평가 금액
        double returnRate,
        List<InvestmentDetail> investments  // 보유 주식 목록
) {
    public static MyInvestmentResponse from(CurrentPlayersInfo playerInfo, List<InvestmentDetail> investments) {
        return new MyInvestmentResponse(
                playerInfo.getBalance(),
                playerInfo.getNetAssets(),
                playerInfo.getValuationAmount(),
                playerInfo.getReturnRate(),
                investments
        );
    }
}
