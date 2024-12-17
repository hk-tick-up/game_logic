package com.tickup.gamelogic.stocksettings.response;

import com.tickup.gamelogic.playersinfo.domain.PlayerClusterLog;

import java.util.Date;

public record GameHistoryResponse(
        Long gameRoomId,
        Date gameDate,
        int cluster
) {
    public static GameHistoryResponse from(PlayerClusterLog log) {
        return new GameHistoryResponse(log.getGameRoomId(), log.getGameDate(), log.getCluster());
    }
}
