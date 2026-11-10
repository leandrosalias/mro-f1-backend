package com.portfolio.mro_f1_backend.common.util;

import com.portfolio.mro_f1_backend.domain.model.Driver;
import com.portfolio.mro_f1_backend.domain.model.TeamUpgrade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BallastCalculatorUtil {

    private static final BigDecimal LITERS_MULTIPLIER = new BigDecimal("2.4");

    private BallastCalculatorUtil() {
    }

    public static int calculateDriverBallastKg(Driver driver, List<TeamUpgrade> teamUpgrades) {
        int upgradeReductionKg = teamUpgrades.stream()
                .filter(tu -> tu.isInstalledOnDriver(driver.getId()))
                .mapToInt(tu -> tu.getUpgrade().getWeightReductionKg())
                .sum();

        int totalKg = driver.getBaseBallastKg() + driver.getPenaltyBallastKg() - upgradeReductionKg;
        return Math.max(0, totalKg);
    }

    public static BigDecimal calculateTrainingLiters(int ballastKg) {
        return BigDecimal.valueOf(ballastKg)
                .multiply(LITERS_MULTIPLIER)
                .setScale(1, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateDriverTrainingLiters(Driver driver, List<TeamUpgrade> teamUpgrades) {
        int ballastKg = calculateDriverBallastKg(driver, teamUpgrades);
        return calculateTrainingLiters(ballastKg);
    }

}
