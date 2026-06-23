package com.bitly.dtos.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bitly.enums.TransactionType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransactionCreateRequest(

        @NotNull(message = "Account id is mandatory") Long accountId,

        @NotNull(message = "Category id is mandatory") Long categoryId,

        @NotNull(message = "Type is mandatory") TransactionType transactionType,

        @NotNull(message = "Amount is mandatory") @Positive(message = "Amount cannot be negative ") @Digits(integer = 12, fraction = 2, message = "The balance format must be up to 12 integers digits and 2 decimals") BigDecimal amount,

        @NotBlank(message = "Description is mandatory and cannot be blank") @Size(min = 4, max = 100, message = "Description must be between 4 and 100 characters") String description,
        LocalDateTime date

) {

}
