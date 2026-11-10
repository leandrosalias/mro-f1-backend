package com.portfolio.mro_f1_backend.service.workshop.validator;

import com.portfolio.mro_f1_backend.common.exception.BusinessRuleException;
import com.portfolio.mro_f1_backend.domain.model.TeamUpgrade;
import com.portfolio.mro_f1_backend.domain.model.UpgradeCatalog;
import com.portfolio.mro_f1_backend.service.workshop.UpgradePurchaseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class TechTreeLockValidator implements UpgradePurchaseValidator {

    @Override
    public void validate(UpgradePurchaseContext context) {
        UpgradeCatalog catalog = context.getCatalogItem();
        TeamUpgrade currentUpgrade = context.getExistingCurrentLevelUpgrade();

        if (currentUpgrade != null) {
            if (currentUpgrade.isCopiedToBothCars()) {
                throw new BusinessRuleException(
                        "Ambos monoplazas ya tienen instalado " + catalog.getBranch() + " Nivel " + catalog.getLevel() + "."
                );
            }
            if (currentUpgrade.isInstalledOnDriver(context.getTargetDriver().getId())) {
                throw new BusinessRuleException(
                        "El piloto " + context.getTargetDriver().getMroDriverName() + " ya tiene instalada la pieza I+D de este nivel."
                );
            }
        }

        if (catalog.getLevel() > 1) {
            TeamUpgrade prevLevel = context.getPreviousLevelUpgrade();
            if (prevLevel == null || !prevLevel.isCopiedToBothCars()) {
                int requiredLevel = catalog.getLevel() - 1;
                throw new BusinessRuleException(
                        "Regla candado: Ambos monoplazas deben tener instalado " + catalog.getBranch()
                                + " Nivel " + requiredLevel + " antes de desbloquear el Nivel " + catalog.getLevel() + "."
                );
            }
        }
    }
}
