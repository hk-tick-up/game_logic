package com.tickup.gamelogic.gamerooms.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRooms, String> {
}
