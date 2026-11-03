package com.portfolio.mro_f1_backend.domain.model;

import com.portfolio.mro_f1_backend.domain.enums.IncidentSeverity;
import com.portfolio.mro_f1_backend.domain.enums.RaceStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "race_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "finish_position")
    private Integer finishPosition;

    @Builder.Default
    @Column(name = "mro_points")
    private Integer mroPoints = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "race_status", nullable = false, length = 30)
    private RaceStatus raceStatus;

    @Builder.Default
    @Column(name = "pole_position", nullable = false)
    private boolean polePosition = false;

    @Builder.Default
    @Column(name = "fastest_lap", nullable = false)
    private boolean fastestLap = false;

    @Builder.Default
    @Column(name = "driver_of_the_day", nullable = false)
    private boolean driverOfTheDay = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "incident_severity", nullable = false, length = 30)
    private IncidentSeverity incidentSeverity = IncidentSeverity.NONE;
}
