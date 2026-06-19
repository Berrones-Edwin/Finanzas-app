package com.bitly.dtos;

import java.math.BigDecimal;

public record AccountBalanceResponse(
    BigDecimal balance,
    String currency
) {
}
