package com.portfolio.mro_f1_backend.service.workshop;

import com.portfolio.mro_f1_backend.common.exception.ResourceNotFoundException;
import com.portfolio.mro_f1_backend.domain.enums.TransactionType;
import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import com.portfolio.mro_f1_backend.domain.model.*;
import com.portfolio.mro_f1_backend.repository.*;
import com.portfolio.mro_f1_backend.service.ledger.LedgerService;
import com.portfolio.mro_f1_backend.service.workshop.validator.UpgradePurchaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpgradeService {

    private static final BigDecimal ATR_MULTIPLIER = new BigDecimal("0.85"); // 15% de descuento

    private final TeamRepository teamRepository;
    private final DriverRepository driverRepository;
    private final UpgradeCatalogRepository catalogRepository;
    private final TeamUpgradeRepository teamUpgradeRepository;
    private final RaceRepository raceRepository;
    private final LedgerService ledgerService;
    private final List<UpgradePurchaseValidator> validators;

    @Transactional
    public TeamUpgrade purchaseOrCopyUpgrade(Long teamId, Long targetDriverId, UpgradeBranch branch, int level) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Escudería no encontrada con ID: " + teamId));

        Driver targetDriver = driverRepository.findById(targetDriverId)
                .orElseThrow(() -> new ResourceNotFoundException("Piloto no encontrado con ID: " + targetDriverId));

        UpgradeCatalog catalogItem = catalogRepository.findByBranchAndLevel(branch, level)
                .orElseThrow(() -> new ResourceNotFoundException("Mejora no encontrada: " + branch + " Nivel " + level));

        TeamUpgrade existingCurrent = teamUpgradeRepository
                .findByTeamIdAndUpgradeId(teamId, catalogItem.getId())
                .orElse(null);

        TeamUpgrade previousLevel = null;
        if (level > 1) {
            previousLevel = teamUpgradeRepository
                    .findByTeamIdAndUpgradeBranchAndUpgradeLevel(teamId, branch, level - 1)
                    .orElse(null);
        }

        Race activeRace = raceRepository.findFirstByIsSettledFalseOrderByRoundNumberAsc().orElse(null);

        boolean isRdPurchase = (existingCurrent == null);
        BigDecimal effectiveCost = calculateEffectiveCost(catalogItem, team.isHasAtrDiscount(), isRdPurchase);

        UpgradePurchaseContext context = UpgradePurchaseContext.builder()
                .team(team)
                .targetDriver(targetDriver)
                .catalogItem(catalogItem)
                .existingCurrentLevelUpgrade(existingCurrent)
                .previousLevelUpgrade(previousLevel)
                .activeRace(activeRace)
                .effectiveCost(effectiveCost)
                .evaluationTime(OffsetDateTime.now())
                .build();

        // Ejecución del patrón Chain of Responsibility
        for (UpgradePurchaseValidator validator : validators) {
            validator.validate(context);
        }

        // Si el equipo estaba bajo comodín de doble ausencia con aviso, fijamos el piloto elegido
        if (team.isSingleCarWildcard() && team.getWildcardUsedDriverId() == null) {
            team.setWildcardUsedDriverId(targetDriver.getId());
        }

        TeamUpgrade savedUpgrade;
        TransactionType txType;
        String txDescription;

        if (isRdPurchase) {
            TeamUpgrade newUpgrade = TeamUpgrade.builder()
                    .team(team)
                    .upgrade(catalogItem)
                    .car1Driver(targetDriver)
                    .rdPurchasedAt(OffsetDateTime.now())
                    .build();
            savedUpgrade = teamUpgradeRepository.save(newUpgrade);
            txType = TransactionType.UPGRADE_RD;
            txDescription = String.format("I+D %s Nivel %d (Pieza #1 -> %s)%s",
                    branch, level, targetDriver.getMroDriverName(),
                    team.isHasAtrDiscount() ? " [15% ATR]" : "");
        } else {
            existingCurrent.setCar2Driver(targetDriver);
            existingCurrent.setCopyPurchasedAt(OffsetDateTime.now());
            savedUpgrade = teamUpgradeRepository.save(existingCurrent);
            txType = TransactionType.UPGRADE_COPY;
            txDescription = String.format("Copia %s Nivel %d (Pieza #2 -> %s)",
                    branch, level, targetDriver.getMroDriverName());
        }

        ledgerService.recordTransaction(team, activeRace, txType, effectiveCost.negate(), txDescription);

        return savedUpgrade;
    }

    public BigDecimal calculateEffectiveCost(UpgradeCatalog catalogItem, boolean hasAtrDiscount, boolean isRdPurchase) {
        if (!isRdPurchase) {
            return catalogItem.getCopyCost().setScale(2, RoundingMode.HALF_UP);
        }
        if (hasAtrDiscount) {
            return catalogItem.getRdCost()
                    .multiply(ATR_MULTIPLIER)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return catalogItem.getRdCost().setScale(2, RoundingMode.HALF_UP);
    }
}
