package com.tickup.gamelogic.stocksettings.repository;

import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameEventsRepository extends JpaRepository<GameEvents, Long> {
}
