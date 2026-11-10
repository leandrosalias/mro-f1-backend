package com.portfolio.mro_f1_backend.service.workshop;

import com.portfolio.mro_f1_backend.common.exception.BusinessRuleException;
import com.portfolio.mro_f1_backend.domain.enums.TransactionType;
import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import com.portfolio.mro_f1_backend.domain.model.*;
import com.portfolio.mro_f1_backend.repository.*;
import com.portfolio.mro_f1_backend.service.ledger.LedgerService;
import com.portfolio.mro_f1_backend.service.workshop.validator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpgradeServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private UpgradeCatalogRepository catalogRepository;
    @Mock private TeamUpgradeRepository teamUpgradeRepository;
    @Mock private RaceRepository raceRepository;
    @Mock private LedgerService ledgerService;

    private UpgradeService upgradeService;

    private Team team;
    private Driver driver1;
    private Driver driver2;
    private UpgradeCatalog powerUnitL1;
    private UpgradeCatalog powerUnitL2;

    @BeforeEach
    void setUp() {
        List<UpgradePurchaseValidator> validators = List.of(
                new MarketOpenValidator(),
                new DriverWorkshopLockValidator(),
                new TechTreeLockValidator(),
                new BudgetValidator()
        );

        upgradeService = new UpgradeService(
                teamRepository, driverRepository, catalogRepository,
                teamUpgradeRepository, raceRepository, ledgerService, validators
        );

        team = Team.builder()
                .id(10L)
                .name("Scuderia Ferrari")
                .currentBudget(new BigDecimal("50.00"))
                .hasAtrDiscount(false)
                .build();

        driver1 = Driver.builder().id(1L).mroDriverName("Leclerc").team(team).workshopLocked(false).build();
        driver2 = Driver.builder().id(2L).mroDriverName("Hamilton").team(team).workshopLocked(false).build();

        powerUnitL1 = UpgradeCatalog.builder()
                .id(7).branch(UpgradeBranch.POWER_UNIT).level(1)
                .weightReductionKg(4).rdCost(new BigDecimal("13.00")).copyCost(new BigDecimal("5.00"))
                .build();

        powerUnitL2 = UpgradeCatalog.builder()
                .id(8).branch(UpgradeBranch.POWER_UNIT).level(2)
                .weightReductionKg(5).rdCost(new BigDecimal("20.00")).copyCost(new BigDecimal("7.00"))
                .build();
    }

    @Test
    @DisplayName("Compra I+D con descuento ATR del 15% cobra $11.05M sobre una pieza de $13M")
    void shouldPurchaseRdWithAtrDiscount() {
        team.setHasAtrDiscount(true);
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver1));
        when(catalogRepository.findByBranchAndLevel(UpgradeBranch.POWER_UNIT, 1)).thenReturn(Optional.of(powerUnitL1));
        when(teamUpgradeRepository.findByTeamIdAndUpgradeId(10L, 7)).thenReturn(Optional.empty());
        when(raceRepository.findFirstByIsSettledFalseOrderByRoundNumberAsc()).thenReturn(Optional.empty());
        when(teamUpgradeRepository.save(any(TeamUpgrade.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamUpgrade result = upgradeService.purchaseOrCopyUpgrade(10L, 1L, UpgradeBranch.POWER_UNIT, 1);

        assertEquals(driver1, result.getCar1Driver());
        assertNull(result.getCar2Driver());
        verify(ledgerService).recordTransaction(
                eq(team), isNull(), eq(TransactionType.UPGRADE_RD),
                eq(new BigDecimal("-11.05")), contains("[15% ATR]")
        );
    }

    @Test
    @DisplayName("Regla Candado: Rechaza investigar Nivel 2 si falta instalar la Copia del Nivel 1 en el segundo auto")
    void shouldRejectLevel2WhenLevel1IsNotCopiedToBothCars() {
        TeamUpgrade incompleteLevel1 = TeamUpgrade.builder()
                .team(team).upgrade(powerUnitL1).car1Driver(driver1).car2Driver(null)
                .build();

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver1));
        when(catalogRepository.findByBranchAndLevel(UpgradeBranch.POWER_UNIT, 2)).thenReturn(Optional.of(powerUnitL2));
        when(teamUpgradeRepository.findByTeamIdAndUpgradeId(10L, 8)).thenReturn(Optional.empty());
        when(teamUpgradeRepository.findByTeamIdAndUpgradeBranchAndUpgradeLevel(10L, UpgradeBranch.POWER_UNIT, 1))
                .thenReturn(Optional.of(incompleteLevel1));
        when(raceRepository.findFirstByIsSettledFalseOrderByRoundNumberAsc()).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                upgradeService.purchaseOrCopyUpgrade(10L, 1L, UpgradeBranch.POWER_UNIT, 2)
        );

        assertTrue(ex.getMessage().contains("Regla candado"));
        verifyNoInteractions(ledgerService);
    }

    @Test
    @DisplayName("Comodín doble ausencia con aviso: permite mejorar el primer auto elegido y bloquea el segundo")
    void shouldEnforceSingleCarWildcardOnDoubleNotifiedAbsence() {
        team.setSingleCarWildcard(true);
        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver1));
        when(driverRepository.findById(2L)).thenReturn(Optional.of(driver2));
        when(catalogRepository.findByBranchAndLevel(UpgradeBranch.POWER_UNIT, 1)).thenReturn(Optional.of(powerUnitL1));
        when(raceRepository.findFirstByIsSettledFalseOrderByRoundNumberAsc()).thenReturn(Optional.empty());

        // 1ra compra para driver1: se permite y fija wildcardUsedDriverId = 1L
        when(teamUpgradeRepository.findByTeamIdAndUpgradeId(10L, 7)).thenReturn(Optional.empty());
        when(teamUpgradeRepository.save(any(TeamUpgrade.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamUpgrade rdUpgrade = upgradeService.purchaseOrCopyUpgrade(10L, 1L, UpgradeBranch.POWER_UNIT, 1);
        assertEquals(1L, team.getWildcardUsedDriverId());

        // 2do intento en la misma fecha para driver2 (comprar la copia): debe rechazarse
        when(teamUpgradeRepository.findByTeamIdAndUpgradeId(10L, 7)).thenReturn(Optional.of(rdUpgrade));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                upgradeService.purchaseOrCopyUpgrade(10L, 2L, UpgradeBranch.POWER_UNIT, 1)
        );
        assertTrue(ex.getMessage().contains("Regla de doble ausencia con aviso"));
    }
}
