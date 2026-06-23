package com.bitly.dtos.transactions;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bitly.enums.TransactionType;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private TransactionCategoryResponse category;
    private TransactionAccountResponse account;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;

    
}
