package com.bitly.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.transactions.TransactionCreateRequest;
import com.bitly.dtos.transactions.TransactionResponse;
import com.bitly.enums.TransactionType;
import com.bitly.services.TransactionService;

import jakarta.validation.Valid;

@RequestMapping("/api/v1/transactions")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TransactionResponse>> getAllTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "type", required = false) TransactionType type,
            @RequestParam(name = "accountId", required = false) Long accountId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(
                transactionService.getTransactions(userDetails.getUsername(), page, size, type, accountId, categoryId,
                        start, end));
    }

    @GetMapping("{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("id") Long id
    ){

        return ResponseEntity.ok(transactionService.getTransaction(userDetails.getUsername(),id));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> saveTransaction(
            @Valid @RequestBody TransactionCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.saveTransaction(request, userDetails.getUsername()));
    }
}
