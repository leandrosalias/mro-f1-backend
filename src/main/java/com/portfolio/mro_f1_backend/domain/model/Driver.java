package com.portfolio.mro_f1_backend.domain.model;

import com.portfolio.mro_f1_backend.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "discord_user_id", unique = true, length = 50)
    private String discordUserId;

    @Column(name = "discord_username", length = 100)
    private String discordUsername;

    @Column(name = "mro_driver_name", nullable = false, unique = true, length = 100)
    private String mroDriverName;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private UserRole role = UserRole.ROLE_DRIVER;

    @Builder.Default
    @Column(name = "is_rookie", nullable = false)
    private boolean isRookie = false;

    @Builder.Default
    @Column(name = "base_ballast_kg", nullable = false)
    private int baseBallastKg = 75;

    @Builder.Default
    @Column(name = "penalty_ballast_kg", nullable = false)
    private int penaltyBallastKg = 0;

    @Builder.Default
    @Column(name = "workshop_locked", nullable = false)
    private boolean workshopLocked = false;
}