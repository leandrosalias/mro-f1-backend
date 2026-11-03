package com.portfolio.mro_f1_backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum SponsorType {
    CONSERVATIVE(new BigDecimal("4.00"), "Ambos autos terminan la carrera"),
    MEDIUM(new BigDecimal("7.00"), "Al menos un auto termina en el Top 5"),
    RISKY(new BigDecimal("12.00"), "Al menos un auto logra Pole Position o Podio");

    private final BigDecimal rewardMillions;
    private final String requirementDescription;
}
