package com.portfolio.mro_f1_backend.service.workshop;

import com.portfolio.mro_f1_backend.domain.model.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
public class UpgradePurchaseContext {

    private final Team team;
    private final Driver targetDriver;
    private final UpgradeCatalog catalogItem;
    private final TeamUpgrade existingCurrentLevelUpgrade; // null si es compra I+D; presente si es Copia
    private final TeamUpgrade previousLevelUpgrade;        // requerido cuando level es mayor a 1
    private final Race activeRace;
    private final BigDecimal effectiveCost;
    private final OffsetDateTime evaluationTime;

    public boolean isRdPurchase() {
        return existingCurrentLevelUpgrade == null;
    }

    public boolean isCopyPurchase() {
        return existingCurrentLevelUpgrade != null;
    }

}
