package com.tickup.gamelogic.playersinfo.repository;

import com.tickup.gamelogic.playersinfo.domain.OwnedShares;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnedSharesRepository extends JpaRepository<OwnedShares, Long> {
    Optional<OwnedShares> findByGameRoomsGameRoomsIdAndUserIdAndTicker(Long gameRoomsId, String userId, String ticker);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OwnedShares o " +
            "WHERE o.gameRooms.gameRoomsId = :gameRoomId " +
            "AND o.userId = :userId "+
            "AND o.ticker = :ticker "
    )
    Optional<OwnedShares> findForUpdate(@Param("gameRoomId") Long gameRoomId,
                                        @Param("userId") String userId,
                                        @Param("ticker") String ticker);


    @Query("SELECT os FROM OwnedShares os " +
            "WHERE os.gameRooms.gameRoomsId = :gameRoomId " +
            "AND os.userId = :userId")
    List<OwnedShares> findAllByGameRoomsIdAndUserId(@Param("gameRoomId") Long gameRoomId, @Param("userId") String userId);

}
