package com.portfolio.mro_f1_backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "mro_team_name", nullable = false, length = 100)
    private String mroTeamName;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Builder.Default
    @Column(name = "color_hex", length = 7)
    private String colorHex = "#FFFFFF";

    @Builder.Default
    @Column(name = "current_budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentBudget = new BigDecimal("50.00");

    @Column(name = "constructors_position")
    private Integer constructorsPosition;

    @Builder.Default
    @Column(name = "has_atr_discount", nullable = false)
    private boolean hasAtrDiscount = false;

    @Builder.Default
    @Column(name = "single_car_wildcard", nullable = false)
    private boolean singleCarWildcard = false;

    @Column(name = "wildcard_used_driver_id")
    private Long wildcardUsedDriverId;
}
