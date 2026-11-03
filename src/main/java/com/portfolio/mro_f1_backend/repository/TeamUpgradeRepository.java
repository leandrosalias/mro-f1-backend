package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import com.portfolio.mro_f1_backend.domain.model.TeamUpgrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamUpgradeRepository extends JpaRepository<TeamUpgrade, Long> {
    List<TeamUpgrade> findByTeamId(Long teamId);
    Optional<TeamUpgrade> findByTeamIdAndUpgradeId(Long teamId, Integer upgradeId);
    Optional<TeamUpgrade> findByTeamIdAndUpgradeBranchAndUpgradeLevel(Long teamId, UpgradeBranch branch, int level);
}
