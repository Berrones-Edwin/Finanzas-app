package com.bitly.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.transfers.TransferCreateRequest;
import com.bitly.dtos.transfers.TransferResponse;
import com.bitly.services.TransferService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/transfers")
@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(
            final TransferService transferService) {
        this.transferService = transferService;

    }

    @GetMapping
    public ResponseEntity<PageResponse<TransferResponse>> getTransfers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "accountId", required = false) Long accountId,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(
                transferService.getTransfer(userDetails.getUsername(), page, size, accountId, start, end));
    }

    @GetMapping("{id}")
    public ResponseEntity<TransferResponse> getTransferById(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                transferService.getTransferById(id, userDetails.getUsername()));

    }

    @PostMapping
    public ResponseEntity<TransferResponse> saveTransfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        transferService.saveTransfer(userDetails.getUsername(), request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTransfer(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        transferService.deleteTransfer(id, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }

}
