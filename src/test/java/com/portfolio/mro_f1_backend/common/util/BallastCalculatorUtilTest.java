package com.portfolio.mro_f1_backend.common.util;

import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import com.portfolio.mro_f1_backend.domain.model.Driver;
import com.portfolio.mro_f1_backend.domain.model.TeamUpgrade;
import com.portfolio.mro_f1_backend.domain.model.UpgradeCatalog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BallastCalculatorUtilTest {

    @Test
    @DisplayName("Piloto sin mejoras ni sanciones inicia con 75 kg y 180.0 litros")
    void shouldCalculateBaseBallastAndLiters() {
        Driver driver = Driver.builder().id(1L).baseBallastKg(75).penaltyBallastKg(0).build();

        int kg = BallastCalculatorUtil.calculateDriverBallastKg(driver, List.of());
        BigDecimal liters = BallastCalculatorUtil.calculateDriverTrainingLiters(driver, List.of());

        assertEquals(75, kg);
        assertEquals(new BigDecimal("180.0"), liters);
    }

    @Test
    @DisplayName("Descuenta solo las piezas instaladas en ese piloto y suma penalizaciones por ausencia")
    void shouldCalculateBallastWithUpgradesAndPenalties() {
        Driver driver1 = Driver.builder().id(1L).baseBallastKg(75).penaltyBallastKg(3).build(); // +3 kg sanción
        Driver driver2 = Driver.builder().id(2L).baseBallastKg(75).penaltyBallastKg(0).build();

        UpgradeCatalog chassisL1 = UpgradeCatalog.builder().branch(UpgradeBranch.CHASSIS).level(1).weightReductionKg(5).build();
        UpgradeCatalog aeroL1 = UpgradeCatalog.builder().branch(UpgradeBranch.AERO).level(1).weightReductionKg(4).build();

        // Chasis L1 en ambos autos (-5 kg), Aero L1 solo en driver2 (-4 kg)
        TeamUpgrade tu1 = TeamUpgrade.builder().upgrade(chassisL1).car1Driver(driver1).car2Driver(driver2).build();
        TeamUpgrade tu2 = TeamUpgrade.builder().upgrade(aeroL1).car1Driver(driver2).car2Driver(null).build();

        int driver1Kg = BallastCalculatorUtil.calculateDriverBallastKg(driver1, List.of(tu1, tu2));
        BigDecimal driver1Liters = BallastCalculatorUtil.calculateDriverTrainingLiters(driver1, List.of(tu1, tu2));

        // 75 base + 3 sanción - 5 Chasis = 73 kg -> 73 * 2.4 = 175.2 L
        assertEquals(73, driver1Kg);
        assertEquals(new BigDecimal("175.2"), driver1Liters);
    }
}
