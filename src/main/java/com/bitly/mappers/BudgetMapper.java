package com.bitly.mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.bitly.dtos.budgets.BudgetCategoryResponse;
import com.bitly.dtos.budgets.BudgetResponse;
import com.bitly.models.Budget;

@Component
public class BudgetMapper {

    public BudgetResponse toDTO(Budget b, BigDecimal spentAmount) {

        BigDecimal planned = b.getAmount();
        BigDecimal remaining = planned.subtract(spentAmount);

        BigDecimal percentageUsed = spentAmount.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : spentAmount
                        .divide(planned, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

        BudgetCategoryResponse c = new BudgetCategoryResponse(
                b.getCategory().getName(),
                b.getCategory().getColor());

        return BudgetResponse.builder()
                .id(b.getId())
                .category(c)
                .month(b.getMonth())
                .plannedAmount(planned)
                .spentAmount(spentAmount)
                .remainingAmount(remaining)
                .percentageUsed(percentageUsed)
                .isAlertSent(b.getIsAlertSent())
                .build();
    }
}
