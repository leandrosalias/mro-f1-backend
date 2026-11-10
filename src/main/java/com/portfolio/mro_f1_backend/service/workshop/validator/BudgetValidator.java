package com.portfolio.mro_f1_backend.service.workshop.validator;

import com.portfolio.mro_f1_backend.common.exception.BusinessRuleException;
import com.portfolio.mro_f1_backend.service.workshop.UpgradePurchaseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(4)
public class BudgetValidator implements UpgradePurchaseValidator {

    @Override
    public void validate(UpgradePurchaseContext context) {
        BigDecimal currentBudget = context.getTeam().getCurrentBudget();
        BigDecimal cost = context.getEffectiveCost();

        if (currentBudget.compareTo(cost) < 0) {
            throw new BusinessRuleException(
                    String.format("Presupuesto insuficiente. Saldo actual: $%sM, Costo requerido: $%sM.",
                            currentBudget.toPlainString(), cost.toPlainString())
            );
        }
    }
}
