package com.portfolio.mro_f1_backend.service.workshop.validator;

import com.portfolio.mro_f1_backend.common.exception.BusinessRuleException;
import com.portfolio.mro_f1_backend.domain.model.Driver;
import com.portfolio.mro_f1_backend.domain.model.Team;
import com.portfolio.mro_f1_backend.service.workshop.UpgradePurchaseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DriverWorkshopLockValidator implements UpgradePurchaseValidator {

    @Override
    public void validate(UpgradePurchaseContext context) {
        Team team = context.getTeam();
        Driver driver = context.getTargetDriver();

        if (driver.getTeam() == null || !driver.getTeam().getId().equals(team.getId())) {
            throw new BusinessRuleException("El piloto seleccionado no pertenece a la escudería " + team.getName() + ".");
        }

        if (team.isSingleCarWildcard()) {
            if (driver.isWorkshopLocked()) {
                // Si el bloqueo proviene de una ausencia sin aviso individual, no puede usar el comodín
                throw new BusinessRuleException(
                        "El piloto " + driver.getMroDriverName() + " tiene el taller bloqueado."
                );
            }
            Long chosenDriverId = team.getWildcardUsedDriverId();
            if (chosenDriverId != null && !chosenDriverId.equals(driver.getId())) {
                throw new BusinessRuleException(
                        "Regla de doble ausencia con aviso: ya elegiste mejorar el monoplaza del otro piloto en esta fecha."
                );
            }
            return;
        }

        if (driver.isWorkshopLocked()) {
            throw new BusinessRuleException(
                    "El piloto " + driver.getMroDriverName() + " tiene el taller bloqueado por ausencia en la fecha anterior."
            );
        }
    }
}
