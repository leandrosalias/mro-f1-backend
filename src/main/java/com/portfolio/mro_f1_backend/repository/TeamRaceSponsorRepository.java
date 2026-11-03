package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.TeamRaceSponsor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRaceSponsorRepository extends JpaRepository<TeamRaceSponsor, Long> {
    Optional<TeamRaceSponsor> findByTeamIdAndRaceId(Long teamId, Long raceId);
    List<TeamRaceSponsor> findByRaceId(Long raceId);
}
