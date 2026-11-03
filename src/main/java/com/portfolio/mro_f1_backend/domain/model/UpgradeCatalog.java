package com.portfolio.mro_f1_backend.domain.model;

import com.portfolio.mro_f1_backend.domain.enums.UpgradeBranch;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "upgrade_catalog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpgradeCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "branch", nullable = false, length = 30)
    private UpgradeBranch branch;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "weight_reduction_kg", nullable = false)
    private int weightReductionKg;

    @Column(name = "rd_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal rdCost;

    @Column(name = "copy_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal copyCost;
}
