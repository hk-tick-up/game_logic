package com.tickup.gamelogic.playersinfo.Repository;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentPlayersInfoRepository extends JpaRepository<CurrentPlayersInfo, Long> {
}
