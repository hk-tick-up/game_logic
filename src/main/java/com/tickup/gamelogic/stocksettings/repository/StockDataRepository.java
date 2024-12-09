package com.tickup.gamelogic.stocksettings.repository;

import com.tickup.gamelogic.stocksettings.domain.CompanyInfo;
import com.tickup.gamelogic.stocksettings.domain.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
    @Query("SELECT sd " +
            "FROM StockData sd " +
            "JOIN FETCH sd.gameRooms gr " +
            "LEFT JOIN FETCH gr.gameEvents ge " +
            "WHERE sd.gameRooms.gameRoomsId = :gameRoomId " +
            "AND sd.turn = :turn " +
            "AND ge.turn = :turn " +
            "AND sd.ticker IN :tickers")
    List<StockData> findTurnDataWithEventsByGameRoomIdAndTurnAndTickersIn(
            @Param("gameRoomId") Long gameRoomId,
            @Param("turn") int turn,
            @Param("tickers") List<String> tickers
    );
}
