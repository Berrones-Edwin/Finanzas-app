package com.bitly.dtos.dashboard;

import java.math.BigDecimal;

public record DashboardByAccountResponse(
    Long accountId,
    String accountName,
    BigDecimal amount
) {

}
