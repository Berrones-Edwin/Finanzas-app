package com.bitly.dtos.dashboard;

import java.math.BigDecimal;

public record DashboardByCategoryResponse(
    Long categoryId,
    String categoryName,
    BigDecimal amount
) {

}
