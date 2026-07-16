package com.bitly.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.AccountBalanceResponse;
import com.bitly.dtos.AccountCreateRequest;
import com.bitly.dtos.AccountResponse;
import com.bitly.dtos.AccountUpdateRequest;
import com.bitly.dtos.PageResponse;
import com.bitly.services.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/api/v1/accounts")
@RestController
@Tag(name = "Account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(
            AccountService accountService) {

        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AccountResponse>> getAllAcounts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "includeInactive", defaultValue = "false", required = false) boolean includeInactive) {

        return ResponseEntity.ok(accountService.getAllAccounts(userDetails.getUsername(), page, size, includeInactive));
    }

    @GetMapping("{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
        @PathVariable("accountId") long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {

        return  ResponseEntity.ok(accountService.getAccountById(userDetails.getUsername(),id));
    }

    @GetMapping("{accountId}/balance")
    public ResponseEntity<AccountBalanceResponse> getBalanceAccount(
        @PathVariable("accountId") long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(accountService.getBalanceAccount(userDetails.getUsername(),id));
    }
    

    @PostMapping
    public ResponseEntity<AccountResponse> saveAccount(
        @Valid @RequestBody AccountCreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
            accountService.saveAccount(request,userDetails.getUsername())
        );
    }

    @PatchMapping("{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(
        @Valid @RequestBody AccountUpdateRequest request,
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("accountId") long id
    ){
        return ResponseEntity.ok(accountService.updateAccount(request,userDetails.getUsername(),id));
    }

    @DeleteMapping("{accountId}")
    public ResponseEntity<?>deleteAccount(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("accountId") long id
    ){

        accountService.deleteAccount(userDetails.getUsername(),id);
        return ResponseEntity.noContent().build();
    }
    
    

}
