package com.tickup.gamelogic.playersinfo.Repository;

import com.tickup.gamelogic.playersinfo.domain.CurrentPlayersInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentPlayersInfoRepository extends JpaRepository<CurrentPlayersInfo, Long> {
}
