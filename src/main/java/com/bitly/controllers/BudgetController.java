package com.bitly.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.budgets.BudgetCreateRequest;
import com.bitly.dtos.budgets.BudgetResponse;
import com.bitly.dtos.budgets.BudgetUpdateRequest;
import com.bitly.services.BudgetService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/v1/budgets")
@RestController
@Tag(name = "Budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("{id}")
    public ResponseEntity<BudgetResponse> getTransactionById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                budgetService.getBudgetById(id, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BudgetResponse>> getAllBudgets(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {

        return ResponseEntity.ok(
                budgetService.getAllBudgetsByMonthAndYear(userDetails.getUsername(), year, month, page, size));
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> saveBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        budgetService.saveBudget(userDetails.getUsername(), request));
    }

    @PatchMapping("{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetUpdateRequest request,
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(budgetService.updateBudget(userDetails.getUsername(), request, id));

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBudget(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable("id") Long id
    ){


        budgetService.deleteBudget(userDetails.getUsername(),id);
        return ResponseEntity.noContent().build();
    }


}
