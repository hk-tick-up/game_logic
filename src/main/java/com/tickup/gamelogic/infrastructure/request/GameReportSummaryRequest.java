package com.tickup.gamelogic.infrastructure.request;

import com.tickup.gamelogic.playersinfo.response.ReportResponse;

import java.time.LocalDateTime;

public record GameReportSummaryRequest(
        String userId,
        Long gameId,
        int clusters,
        String message,
        LocalDateTime gameEndTime
) {
    public static GameReportSummaryRequest from (ReportResponse response, String userId, Long gameId) {
        return new GameReportSummaryRequest(
                userId,
                gameId,
                response.clusters(),
                response.message(),
                LocalDateTime.now()
        );
    }
}
