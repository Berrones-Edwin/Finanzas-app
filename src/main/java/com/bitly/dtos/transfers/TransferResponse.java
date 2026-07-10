package com.bitly.dtos.transfers;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferResponse(
    Long id,
    BigDecimal amount,
    String description,
    LocalDate date,
    TransferAccountResponse fromAccount,
    TransferAccountResponse toAccount
) {

}
