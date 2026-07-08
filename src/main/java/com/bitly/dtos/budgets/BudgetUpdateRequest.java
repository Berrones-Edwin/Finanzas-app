package com.bitly.dtos.budgets;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BudgetUpdateRequest(
        @NotNull(message = "Amount is mandatory") @Positive(message = "Amount must be greater than zero") @Digits(integer = 12, fraction = 2, message = "Format must be up to 12 integer digits and 2 decimals") BigDecimal amount,

        @Min(value = 1, message = "Alert threshold must be at least 1%") @Max(value = 100, message = "Alert threshold cannot exceed 100%") Integer alertThreshold,

        @Size(max = 100, message = "Notes cannot exceed 100 characters") String notes) {

}
