package com.bitly.dtos.transfers;

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
public class TransferAccountResponse {

    private Long id;
    private String name;
    private String color;
    private AccountType type;

}
