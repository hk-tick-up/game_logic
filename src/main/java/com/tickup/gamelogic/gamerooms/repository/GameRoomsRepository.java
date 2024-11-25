package com.tickup.gamelogic.gamerooms.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface GameRoomsRepository extends JpaRepository<GameRooms, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE GameRooms g " +
            "SET g.currentTurn = :current_turn, " +
            "    g.currentTurnStartTime = :current_turn_start_time," +
            "    g.remainingTime = :remaining_time " +
            "WHERE g.gameRoomsId = :game_rooms_id")
    void updateGameRoomsState(@Param("game_rooms_id") Long gameRoomsId,
                              @Param("curren_turn") int currentTurn,
                              @Param("current_turn_start_time") Instant currentTurnStartTime,
                              @Param("remaining_time") int remainingTime);
}
