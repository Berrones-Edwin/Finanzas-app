package com.bitly.mappers;

import org.springframework.stereotype.Component;

import com.bitly.dtos.transfers.TransferAccountResponse;
import com.bitly.dtos.transfers.TransferResponse;
import com.bitly.models.Transfer;

@Component
public class TransferMapper {

    public TransferResponse toDTO(Transfer t) {

        TransferAccountResponse fromAccount = TransferAccountResponse.builder()
                .id(t.getFromAccount().getId())
                .name(t.getFromAccount().getName())
                .color(t.getFromAccount().getColor())
                .type(t.getFromAccount().getAccountType())
                .build();

        TransferAccountResponse toAccount = TransferAccountResponse.builder()
                .id(t.getToAccount().getId())
                .name(t.getToAccount().getName())
                .color(t.getToAccount().getColor())
                .type(t.getToAccount().getAccountType())
                .build();

        return new TransferResponse(
                t.getId(),
                t.getAmount(),
                t.getDescription(),
                t.getDate(),
                fromAccount,
                toAccount);

    }

}
