package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByDiscordUserId(String discordUserId);
    Optional<Driver> findByMroDriverNameIgnoreCase(String mroDriverName);
    List<Driver> findByTeamId(Long teamId);
}
