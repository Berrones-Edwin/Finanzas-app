package com.bitly.dtos.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    BigDecimal income,
    BigDecimal expenses,
    BigDecimal balance,
    BigDecimal savingsRate
) {

}
