package com.tickup.gamelogic.gamerooms.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomsRepository extends JpaRepository<GameRooms, String> {
}
