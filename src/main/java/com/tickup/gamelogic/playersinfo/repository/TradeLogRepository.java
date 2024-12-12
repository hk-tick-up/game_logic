package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.playersinfo.domain.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
}
