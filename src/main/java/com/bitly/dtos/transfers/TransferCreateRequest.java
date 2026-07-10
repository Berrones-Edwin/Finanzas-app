package com.bitly.dtos.transfers;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransferCreateRequest(
        @NotNull(message = "From account id is mandatory") Long fromAccount,
        @NotNull(message = "To account id is mandatory") Long toAccount,
        @NotNull(message = "Amount is mandatory") @Positive(message = "Amount cannot be negative ") @Digits(integer = 12, fraction = 2, message = "The balance format must be up to 12 integers digits and 2 decimals") BigDecimal amount,
        @Size(min = 4, max = 100, message = "Notes must be between 4 and 100 characters") String description,
        @NotNull(message = "Date is mandatory") LocalDate date) {

}
