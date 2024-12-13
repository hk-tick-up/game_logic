package com.tickup.gamelogic.infrastructure;

import com.tickup.gamelogic.infrastructure.request.GameReportSummaryRequest;
import com.tickup.gamelogic.playersinfo.request.GameReportRequest;
import com.tickup.gamelogic.playersinfo.response.ReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportApiClient {
    private final RestTemplate restTemplate;

    @Value("${report.api.url}")
    private String reportApiUrl;

    @Value("${mypage.api.url}")
    private String mypageUrl;

    public ReportResponse sendTradeLog(List<GameReportRequest> report, String userId, Long gameId) {
        try {
            log.info("Send trade log: {}", report);

            ResponseEntity<ReportResponse> response = restTemplate.postForEntity(
                    reportApiUrl,
                    report,
                    ReportResponse.class
            );

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully received report response: {}", response.getBody());
                sendReportToMyPage(response.getBody(), userId, gameId);
                return response.getBody();
            } else {
                log.error("Failed to get valid response from report API");
                throw new RuntimeException("Invalid response from report API");
            }
        } catch(Exception e) {
            log.error("Failed to send game report: ", e);
            throw new RuntimeException("Failed to send game report", e);
        }
    }

    private void sendReportToMyPage(ReportResponse response, String userId, Long gameId) {
        try {
            GameReportSummaryRequest summary = GameReportSummaryRequest.from(response, userId, gameId);

            restTemplate.postForEntity(
                    mypageUrl,
                    summary,
                    Void.class
            );
            log.info("Successfully sent report to MyPage service for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send report to MyPage service: ", e);
        }
    }

}
