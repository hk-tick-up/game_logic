package com.tickup.gamelogic.ml.repository;

import com.tickup.gamelogic.ml.domain.MLStockEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MLStockEventRepository extends JpaRepository<MLStockEvent, Long> {

}
