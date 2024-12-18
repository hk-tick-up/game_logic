package com.tickup.gamelogic.gamerooms.response;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import com.tickup.gamelogic.stocksettings.domain.StockData;

import java.util.List;

public record GameEndDataResponse(
        // 종료 시 플레이어의 최종 수익률과 랭킹 보내기
        int currentRank,
        double returnRate,

        // 종료 시 마지막 턴 주식 정보 보내기
         List<LastTurnStockInfo> stockDataList

) {
    public static GameEndDataResponse from(CurrentPlayersInfo playerInfo, List<StockData> stockDataList) {
        // StockData -> StockInfo로 변환
        List<LastTurnStockInfo> stockInfos = stockDataList.stream()
                .map(stockData -> new LastTurnStockInfo(
                        stockData.getTicker(),
                        stockData.getCompanyName(),
                        stockData.getStockPrice(),
                        stockData.getChangeRate()
                ))
                .toList();

        return new GameEndDataResponse(
                playerInfo.getCurrentRank(),
                playerInfo.getReturnRate(),
                stockInfos

        );
    }

    public static record LastTurnStockInfo(
            String ticker,
            String companyName,
            int stockPrice,
            double changeRate
    ) {

    }
}
