package com.portfolio.mro_f1_backend.service.workshop.validator;

import com.portfolio.mro_f1_backend.common.exception.BusinessRuleException;
import com.portfolio.mro_f1_backend.domain.model.Race;
import com.portfolio.mro_f1_backend.service.workshop.UpgradePurchaseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class MarketOpenValidator implements UpgradePurchaseValidator {

    @Override
    public void validate(UpgradePurchaseContext context) {
        Race activeRace = context.getActiveRace();
        if (activeRace != null && activeRace.isMarketLocked(context.getEvaluationTime())) {
            throw new BusinessRuleException(
                    "El mercado de mejoras se encuentra cerrado para la Fecha " + activeRace.getRoundNumber() + "."
            );
        }
    }
}
