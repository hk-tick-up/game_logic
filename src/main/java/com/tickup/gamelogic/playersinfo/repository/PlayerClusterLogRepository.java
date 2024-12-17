package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.playersinfo.domain.PlayerClusterLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerClusterLogRepository extends JpaRepository<PlayerClusterLog, Long> {
    @Query("SELECT pcl FROM PlayerClusterLog pcl JOIN FETCH pcl.gameRoomId WHERE pcl.userId = :userId")
    List<PlayerClusterLog> findAllByUserIdWithFetchJoin(String userId);

    // 특정 전적 조회
    PlayerClusterLog findByUserIdAndGameRoomId(String userId, Long gameRoomId);

}
