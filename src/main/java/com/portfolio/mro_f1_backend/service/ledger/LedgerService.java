package com.portfolio.mro_f1_backend.service.ledger;

import com.portfolio.mro_f1_backend.domain.enums.TransactionType;
import com.portfolio.mro_f1_backend.domain.model.LedgerTransaction;
import com.portfolio.mro_f1_backend.domain.model.Race;
import com.portfolio.mro_f1_backend.domain.model.Team;
import com.portfolio.mro_f1_backend.repository.LedgerTransactionRepository;
import com.portfolio.mro_f1_backend.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final TeamRepository teamRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;

    @Transactional
    public LedgerTransaction recordTransaction(
            Team team,
            Race race,
            TransactionType type,
            BigDecimal amount,
            String description
    ) {
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBalance = team.getCurrentBudget()
                .add(normalizedAmount)
                .setScale(2, RoundingMode.HALF_UP);

        team.setCurrentBudget(newBalance);
        teamRepository.save(team);

        LedgerTransaction transaction = LedgerTransaction.builder()
                .team(team)
                .race(race)
                .transactionType(type)
                .amount(normalizedAmount)
                .balanceAfter(newBalance)
                .description(description)
                .build();

        return ledgerTransactionRepository.save(transaction);
    }

}
