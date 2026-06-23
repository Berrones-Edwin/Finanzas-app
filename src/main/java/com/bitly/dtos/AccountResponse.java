package com.bitly.dtos;

import java.time.LocalDateTime;

import com.bitly.enums.AccountType;

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
public class AccountResponse {

    private long id;
    private String name;
    private AccountType accountType;
    private String currency;
    private String color;
    private LocalDateTime createdAt;

}
