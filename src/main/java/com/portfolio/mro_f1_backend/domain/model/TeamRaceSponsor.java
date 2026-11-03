package com.portfolio.mro_f1_backend.domain.model;

import com.portfolio.mro_f1_backend.domain.enums.SponsorType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "team_race_sponsors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRaceSponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Enumerated(EnumType.STRING)
    @Column(name = "sponsor_type", nullable = false, length = 20)
    private SponsorType sponsorType;

    @Column(name = "target_met")
    private Boolean targetMet;

    @Column(name = "reward_paid", precision = 10, scale = 2)
    private BigDecimal rewardPaid;

    @Builder.Default
    @Column(name = "signed_at", nullable = false)
    private OffsetDateTime signedAt = OffsetDateTime.now();
}
