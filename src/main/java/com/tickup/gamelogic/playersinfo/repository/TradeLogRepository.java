package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import com.tickup.gamelogic.playersinfo.domain.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
    @Query("SELECT tl FROM TradeLog tl " +
            "WHERE tl.gameRooms = :gameRoom " +
            "AND tl.userId = :userId " +
            "ORDER BY tl.turn ASC"
    )
    List<TradeLog> findAllByGameRoomsAndUserIdOrderByTurnAsc(
            @Param("gameRoom") GameRooms gameRooms,
            @Param("userId") String userId
    );

}
