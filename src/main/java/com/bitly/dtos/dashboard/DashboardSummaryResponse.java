package com.bitly.dtos.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    BigDecimal income,
    BigDecimal expensess,
    BigDecimal balance,
    BigDecimal savingsRate
) {

}
