package com.bitly.dtos.transactions;

import java.math.BigDecimal;

import com.bitly.enums.AccountType;

public record TransactionAccountResponse(
        long id,
        String name,
        AccountType accountType,
        String currency,
        String color,
        BigDecimal balance) {

}
