package com.bitly.dtos.budgets;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BudgetCreateRequest(
                @NotNull(message = "Category id is mandatory") Long categoryId,
                @NotNull(message = "Month is mandatory") LocalDate month,
                @NotNull(message = "Amount is mandatory") @Positive(message = "Amount cannot be negative ") @Digits(integer = 12, fraction = 2, message = "The balance format must be up to 12 integers digits and 2 decimals") BigDecimal amount,
                @Min(value = 1, message = "Alert Threshold must be at least 1%") @Max(value = 100, message = "Alert Threshold cannot exceed 100%") Integer alertThreshold,
                @Size(min = 4, max = 100, message = "Notes must be between 4 and 100 characters") String notes) {

}
