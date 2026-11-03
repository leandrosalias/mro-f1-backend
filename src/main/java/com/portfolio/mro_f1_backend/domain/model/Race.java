package com.portfolio.mro_f1_backend.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "races")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "round_number", nullable = false, unique = true)
    private int roundNumber;

    @Column(name = "circuit_name", nullable = false, length = 100)
    private String circuitName;

    @Column(name = "mro_championship_code", nullable = false, length = 20)
    private String mroChampionshipCode;

    @Column(name = "mro_race_id", nullable = false, length = 20)
    private String mroRaceId;

    @Column(name = "race_datetime")
    private OffsetDateTime raceDatetime;

    @Column(name = "manual_market_lock")
    private Boolean manualMarketLock;

    @Builder.Default
    @Column(name = "is_settled", nullable = false)
    private boolean isSettled = false;

    public boolean isMarketLocked(OffsetDateTime now) {
        if (this.isSettled) {
            return true;
        }
        if (this.manualMarketLock != null) {
            return this.manualMarketLock;
        }
        if (this.raceDatetime != null) {
            OffsetDateTime lockThreshold = this.raceDatetime.minusHours(12);
            return !now.isBefore(lockThreshold);
        }
        return false;
    }
}
