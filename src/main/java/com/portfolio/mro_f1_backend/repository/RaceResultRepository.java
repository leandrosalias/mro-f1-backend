package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    List<RaceResult> findByRaceIdOrderByFinishPositionAsc(Long raceId);
    List<RaceResult> findByRaceIdAndDriverTeamId(Long raceId, Long teamId);
}
