package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import com.portfolio.mro_f1_backend.domain.model.UpgradeCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UpgradeCatalogRepository extends JpaRepository<UpgradeCatalog, Integer> {
    Optional<UpgradeCatalog> findByBranchAndLevel(UpgradeBranch branch, int level);
    List<UpgradeCatalog> findAllByOrderByBranchAscLevelAsc();
}
