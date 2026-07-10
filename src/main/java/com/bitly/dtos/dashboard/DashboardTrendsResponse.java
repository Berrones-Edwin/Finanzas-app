package com.bitly.dtos.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DashboardTrendsResponse(
                LocalDateTime yearmonth,
                LocalDateTime month,
                BigDecimal income,
                BigDecimal expense

) {

}
