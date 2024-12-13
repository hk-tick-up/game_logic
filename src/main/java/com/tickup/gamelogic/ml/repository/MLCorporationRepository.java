package com.tickup.gamelogic.ml.repository;

import com.tickup.gamelogic.ml.domain.MLCorporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MLCorporationRepository extends JpaRepository<MLCorporation, Long> {
//    @Query("SELECT c FROM MLCorporation c")
//    List<String> findAllCorps();
//    @Query("SELECT c.corpTicker FROM MLCorporation c")
//    List<String> findAllCorpTickers();
}
