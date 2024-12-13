package com.tickup.gamelogic.playersinfo.response;

import com.tickup.gamelogic.playersinfo.domain.OwnedShares;

public record InvestmentDetail(
        String ticker,
        String companyName,
        int shares,
        int pricePerShare,     // 현재 주가
        int valuationAmount,    // 평가 금액
        int averagePurchasePrice, // 평균 매수가
        double rateReturn       // 수익률
) {
    public static InvestmentDetail from(OwnedShares ownedShares, int currentPrice) {
        return new InvestmentDetail(
                ownedShares.getTicker(),
                ownedShares.getCompanyName(),
                ownedShares.getShares(),
                currentPrice, // 현재 주가
                ownedShares.getShares() * currentPrice, // 평가 금액
                ownedShares.getAveragePurchasePrice(),  // 평균 매수가
                ownedShares.getRateReturn()             // 수익률
        );
    }
}
