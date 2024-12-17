package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.playersinfo.domain.TradeCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeClusterRepository extends JpaRepository<TradeCluster, Long> {
    @Query("SELECT tc FROM TradeCluster tc " +
            "WHERE tc.tradeClusterId = :tradeClusterId"
    )
    public TradeCluster findByTradeClusterId(@Param("tradeClusterId") int tradeClusterId);

}
