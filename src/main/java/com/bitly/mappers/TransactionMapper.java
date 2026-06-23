package com.bitly.mappers;

import org.springframework.stereotype.Component;

import com.bitly.dtos.transactions.TransactionAccountResponse;
import com.bitly.dtos.transactions.TransactionCategoryResponse;
import com.bitly.dtos.transactions.TransactionResponse;


import com.bitly.models.Account;
import com.bitly.models.Category;
import com.bitly.models.Transaction;

@Component
public class TransactionMapper {

    
    public TransactionResponse toDTO (Transaction transaction){

        Category category = transaction.getCategory();
        Account account = transaction.getAccount();

        TransactionCategoryResponse c = new TransactionCategoryResponse(
            category.getId(),
            category.getName(),
            category.getColor(),
            category.getCategoryType()
        );

        TransactionAccountResponse a = new TransactionAccountResponse(
            account.getId(),
            account.getName(),
            account.getAccountType(),
            account.getCurrency().getCurrencyCode(),
            account.getColor(),
            account.getBalance()
        );

        TransactionResponse response = TransactionResponse.builder()
        .id(transaction.getId())
        .category(c)
        .account(a)
        .transactionType(transaction.getTransactionType())
        .amount(transaction.getAmount())
        .description(transaction.getDescription())
        .date(transaction.getDate())
        .build();

        return response;
    }

}
