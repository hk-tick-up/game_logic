package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentPlayersInfoRepository extends JpaRepository<CurrentPlayersInfo, Long> {
    @Query("SELECT p FROM CurrentPlayersInfo p " +
            "WHERE p.gameRooms.gameRoomsId = :gameRoomId " +
            "AND p.userId = :userId"
    )
    Optional<CurrentPlayersInfo> findByGameRoomIdAndUserId(@Param("gameRoomId") Long gameRoomId, @Param("userId") String userId);

}
