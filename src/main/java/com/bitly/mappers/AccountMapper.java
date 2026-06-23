package com.bitly.mappers;

import java.util.Currency;

import org.springframework.stereotype.Component;

import com.bitly.dtos.AccountResponse;
import com.bitly.models.Account;

@Component
public class AccountMapper {


    public AccountResponse toDTO(Account a){

        return AccountResponse.builder()
        .id(a.getId())
        .name(a.getName())
        .accountType(a.getAccountType())
        .currency(a.getCurrency().getCurrencyCode())
        .color(a.getColor())
        .createdAt(a.getCreatedAt())
        .build();
    }

    public Currency toCurrency(String code){

        try {
            return Currency.getInstance(code);
        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException("currency code not supported " + code);
        }
    }
}
