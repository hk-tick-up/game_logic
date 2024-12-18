package com.tickup.gamelogic.gamerooms.repository;

import com.tickup.gamelogic.gamerooms.domain.GameRules;
import com.tickup.gamelogic.gamerooms.domain.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRulesRepository extends JpaRepository<GameRules, Long> {

    @Query("SELECT gr FROM GameRules gr WHERE gr.gameType = :game_type")
    GameRules findGameRulesByGameType(@Param("game_type") GameType gameType);

}
