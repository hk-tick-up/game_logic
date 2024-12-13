package com.tickup.gamelogic.ml.repository;

import com.tickup.gamelogic.ml.domain.MLStockEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MLStockEventRepository extends JpaRepository<MLStockEvent, Long> {
    List<MLStockEvent> findAllByCorpTicker(String corpTicker);
}
