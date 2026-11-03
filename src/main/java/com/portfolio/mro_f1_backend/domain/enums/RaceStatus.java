package com.portfolio.mro_f1_backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum RaceStatus {
    FINISHED(new BigDecimal("10.00"), 0, false, true),
    DNF(new BigDecimal("7.00"), 0, false, false),
    ABSENT_NOTIFIED(new BigDecimal("5.00"), 0, true, false),
    ABSENT_UNNOTIFIED(new BigDecimal("3.00"), 3, true, false);

    private final BigDecimal finishBonusMillions;
    private final int penaltyBallastKg;
    private final boolean locksWorkshopNextRace;
    private final boolean eligibleForPositionPrize;
}
