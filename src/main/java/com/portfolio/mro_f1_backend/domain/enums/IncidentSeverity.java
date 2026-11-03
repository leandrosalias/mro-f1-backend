package com.portfolio.mro_f1_backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum IncidentSeverity {
    NONE(BigDecimal.ZERO),
    MINOR(new BigDecimal("-2.00")),
    AVOIDABLE_COLLISION(new BigDecimal("-5.00")),
    SEVERE(new BigDecimal("-10.00"));

    private final BigDecimal fineMillions;
}
