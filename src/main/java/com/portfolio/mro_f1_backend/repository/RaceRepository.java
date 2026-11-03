package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.Race;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {
    Optional<Race> findByRoundNumber(int roundNumber);
    Optional<Race> findFirstByIsSettledFalseOrderByRoundNumberAsc();
    List<Race> findAllByOrderByRoundNumberAsc();
}
