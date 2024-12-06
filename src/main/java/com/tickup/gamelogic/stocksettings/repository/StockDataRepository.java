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
    @Query("SELECT sd.ticker as ticker, " +
            "sd.stockPrice as stockPrice, " +
            "sd.changeRate as changeRate, " +
            "ge.eventContents as eventCotents " +
            "FROM StockData sd " +
            "INNER JOIN GameEvents ge ON sd.ticker = ge.ticker " +
            "AND sd.gameRooms.gameRoomsId = ge.gameRooms.gameRoomsId " +
            "AND sd.turn = ge.turn " +
            "WHERE sd.gameRooms.gameRoomsId =: gameRoomId " +
            "AND sd.turn = :turn " +
            "AND sd.ticker IN :tickers"
            )
    List<StockTurnDataProjection> findTurnDataByGameRoomIdAndTurnAndTickersIn(
            @Param("gameRoomId") Long gameRoomId,
            @Param("turn") int turn,
            @Param("tickers") List<String> tickers
    );
}
