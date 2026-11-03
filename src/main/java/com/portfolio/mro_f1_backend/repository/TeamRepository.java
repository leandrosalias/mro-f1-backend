package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByMroTeamNameIgnoreCase(String mroTeamName);
    List<Team> findAllByOrderByConstructorsPositionAsc();
}
