package com.tickup.gamelogic.infrastructure;

import com.tickup.gamelogic.infrastructure.request.GameReportSummaryRequest;
import com.tickup.gamelogic.playersinfo.domain.PlayerClusterLog;
import com.tickup.gamelogic.playersinfo.repository.PlayerClusterLogRepository;
import com.tickup.gamelogic.playersinfo.request.GameReportRequest;
import com.tickup.gamelogic.playersinfo.response.ReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportApiClient {
    private final RestTemplate restTemplate;
    private final PlayerClusterLogRepository playerClusterLogRepository;

    @Value("${report.api.url}")
    private String reportApiUrl;

    @Value("${mypage.api.url}")
    private String mypageUrl;

//    public ReportResponse sendTradeLog(List<GameReportRequest> report, String userId, Long gameId) {
//        try {
//            log.info("Send trade log: {}", report);
//
//            ResponseEntity<ReportResponse> response = restTemplate.postForEntity(
//                    reportApiUrl,
//                    report,
//                    ReportResponse.class
//            );
//
//            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                log.info("Successfully received report response: {}", response.getBody());
//                sendReportToMyPage(response.getBody(), userId, gameId);
//                return response.getBody();
//            } else {
//                log.error("Failed to get valid response from report API");
//                throw new RuntimeException("Invalid response from report API");
//            }
//        } catch(Exception e) {
//            log.error("Failed to send game report: ", e);
//            throw new RuntimeException("Failed to send game report", e);
//        }
//    }
//
//    private void sendReportToMyPage(ReportResponse response, String userId, Long gameId) {
//        try {
//            GameReportSummaryRequest summary = GameReportSummaryRequest.from(response, userId, gameId);
//
//            restTemplate.postForEntity(
//                    mypageUrl,
//                    summary,
//                    Void.class
//            );
//            log.info("Successfully sent report to MyPage service for user: {}", userId);
//        } catch (Exception e) {
//            log.error("Failed to send report to MyPage service: ", e);
//        }
//    }


    /**
     * 외부 API로 trade log를 전송하고 클러스터 ID를 반환받아 저장
     */
    public void processTradeLogAndSaveCluster(List<GameReportRequest> report, String userId, Long gameId) {
        try {
            // 외부 API에 tradelog 전송
            log.info("Sending trade log to external API: {}", report);

            ResponseEntity<ReportResponse> response = restTemplate.postForEntity(
                    reportApiUrl,
                    report,
                    ReportResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                int clusterId = response.getBody().clusters();
                log.info("Successfully received cluster ID: {}", clusterId);

                // 클러스터 로그 저장
                savePlayerClusterLog(userId, gameId, clusterId);

            } else {
                log.error("Failed to get valid cluster ID response from report API");
                throw new RuntimeException("Invalid response from report API");
            }

        } catch (Exception e) {
            log.error("Failed to process trade log and save cluster: ", e);
            throw new RuntimeException("Failed to process trade log and save cluster", e);
        }
    }

    /**
     * 클러스터 로그 저장
     */
    private void savePlayerClusterLog(String userId, Long gameId, int clusterId) {
        PlayerClusterLog playerClusterLog = PlayerClusterLog.builder()
                .userId(userId)
                .gameRoomId(gameId)
                .gameDate(new Date()) // 현재 날짜
                .cluster(clusterId)
                .build();

        playerClusterLogRepository.save(playerClusterLog);
        log.info("Saved PlayerClusterLog: userId={}, gameRoomId={}, clusterId={}", userId, gameId, clusterId);
    }

}
