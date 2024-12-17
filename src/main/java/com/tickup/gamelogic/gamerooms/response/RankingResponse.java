package com.tickup.gamelogic.gamerooms.response;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;

public record RankingResponse(
        String userId,
        String userName,
        int rank,
        double returnRate
) {
    public static RankingResponse from(CurrentPlayersInfo player) {
        return new RankingResponse(
                player.getUserId(),
                player.getUserName(),
                player.getCurrentRank(),
                player.getReturnRate()
        );
    }
}
