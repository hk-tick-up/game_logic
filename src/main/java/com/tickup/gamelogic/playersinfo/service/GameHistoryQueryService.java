package com.tickup.gamelogic.playersinfo.service;

import com.tickup.gamelogic.playersinfo.domain.PlayerClusterLog;
import com.tickup.gamelogic.playersinfo.domain.TradeCluster;
import com.tickup.gamelogic.playersinfo.repository.PlayerClusterLogRepository;
import com.tickup.gamelogic.playersinfo.repository.TradeClusterRepository;
import com.tickup.gamelogic.stocksettings.response.ClusterDetailResponse;
import com.tickup.gamelogic.stocksettings.response.GameHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameHistoryQueryService {
    private final PlayerClusterLogRepository playerClusterLogRepository;
    private final TradeClusterRepository tradeClusterRepository;

    // 게임 전적 목록 조회
    public List<GameHistoryResponse> getUserGameHistory(String userId) {
        return playerClusterLogRepository.findAllByUserIdWithFetchJoin(userId)
                .stream()
                .map(GameHistoryResponse::from)
                .collect(Collectors.toList());
    }

    // 클러스터 상세 정보 조회
    public ClusterDetailResponse getClusterDetails(String userId, Long gameRoomId) {
        PlayerClusterLog log = playerClusterLogRepository.findByUserIdAndGameRoomId(userId, gameRoomId);

        // tradeClusterId를 int 타입으로 그대로 사용
        TradeCluster cluster = tradeClusterRepository.findByTradeClusterId(log.getCluster());

        if (cluster == null) {
            throw new IllegalArgumentException("Cluster not found");
        }

        return ClusterDetailResponse.from(cluster);
    }
}
