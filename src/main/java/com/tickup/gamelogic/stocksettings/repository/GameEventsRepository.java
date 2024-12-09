package com.tickup.gamelogic.stocksettings.repository;

import com.tickup.gamelogic.stocksettings.domain.GameEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameEventsRepository extends JpaRepository<GameEvents, Long> {

}
