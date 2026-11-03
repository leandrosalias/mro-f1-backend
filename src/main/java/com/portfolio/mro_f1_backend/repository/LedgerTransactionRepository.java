package com.portfolio.mro_f1_backend.repository;

import com.portfolio.mro_f1_backend.domain.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
    List<LedgerTransaction> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}
