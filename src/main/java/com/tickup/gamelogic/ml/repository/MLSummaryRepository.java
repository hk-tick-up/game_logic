package com.tickup.gamelogic.ml.repository;

import com.tickup.gamelogic.ml.domain.MLSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MLSummaryRepository extends JpaRepository<MLSummary, Long> {
    @Query("SELECT s.summary FROM MLSummary s WHERE s.eventId = ?1")
    String getSummaryById(int eventId);
}
