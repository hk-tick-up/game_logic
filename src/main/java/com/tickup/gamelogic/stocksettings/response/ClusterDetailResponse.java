package com.tickup.gamelogic.stocksettings.response;

import com.tickup.gamelogic.playersinfo.domain.TradeCluster;

public record ClusterDetailResponse(
        int tradeClusterId,
        String name,
        String tagline,
        String description,
        String traits,
        String remark

) {
    public static ClusterDetailResponse from(TradeCluster cluster) {
        return new ClusterDetailResponse(
                cluster.getTradeClusterId(),
                cluster.getName(),
                cluster.getTagline(),
                cluster.getDescription(),
                cluster.getTraits(),
                cluster.getRemark()
        );
    }
}
