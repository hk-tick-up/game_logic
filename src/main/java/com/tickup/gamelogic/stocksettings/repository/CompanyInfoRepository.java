package com.tickup.gamelogic.stocksettings.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRules;
import com.tickup.gamelogic.gamerooms.domain.GameType;
import com.tickup.gamelogic.stocksettings.domain.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
    @Query("SELECT ci.companyName FROM CompanyInfo ci " +
            "WHERE ci.gameRooms.gameRoomsId = :gameRoomId "+
            "AND ci.ticker = :ticker"
    )
    String findCompanyNameByGameRoomIdAndTicker(@Param("gameRoomId") Long gameRoomId, @Param("ticker") String ticker);

}
