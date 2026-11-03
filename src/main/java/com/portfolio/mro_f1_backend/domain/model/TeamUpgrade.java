package com.portfolio.mro_f1_backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "team_upgrades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUpgrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "upgrade_id", nullable = false)
    private UpgradeCatalog upgrade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_1_driver_id", nullable = false)
    private Driver car1Driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_2_driver_id")
    private Driver car2Driver;

    @Builder.Default
    @Column(name = "rd_purchased_at", nullable = false)
    private OffsetDateTime rdPurchasedAt = OffsetDateTime.now();

    @Column(name = "copy_purchased_at")
    private OffsetDateTime copyPurchasedAt;

    public boolean isCopiedToBothCars() {
        return this.car2Driver != null;
    }

    public boolean isInstalledOnDriver(Long driverId) {
        return (car1Driver != null && car1Driver.getId().equals(driverId))
                || (car2Driver != null && car2Driver.getId().equals(driverId));
    }
}
