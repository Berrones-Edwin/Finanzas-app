package com.bitly.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.dashboard.DashboardByAccountResponse;
import com.bitly.dtos.dashboard.DashboardByCategoryResponse;
import com.bitly.dtos.dashboard.DashboardSummaryResponse;
import com.bitly.dtos.dashboard.DashboardTrendsResponse;
import com.bitly.services.DashboardService;

@RequestMapping("/api/v1/dashboard")
@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            final DashboardService dashboardService) {
        this.dashboardService = dashboardService;

    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) LocalDate start,
            @RequestParam(name = "end", required = false) LocalDate end

    ) {

        return ResponseEntity.ok(dashboardService.getDashboardSummary(userDetails.getUsername(), start, end));
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<DashboardByCategoryResponse>> getExpensesByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) LocalDate start,
            @RequestParam(name = "end", required = false) LocalDate end

    ) {
        return ResponseEntity.ok(dashboardService.getExpensesByCategory(userDetails.getUsername(), start, end));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<DashboardTrendsResponse>> getMonthlyTrends(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) LocalDate start,
            @RequestParam(name = "end", required = false) LocalDate end

    ) {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends(userDetails.getUsername(), start, end));
    }

    @GetMapping("/by-account")
    public ResponseEntity<List<DashboardByAccountResponse>> getBalanceByAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) LocalDate start,
            @RequestParam(name = "end", required = false) LocalDate end) {

        return ResponseEntity.ok(dashboardService.getBalanceAccount(userDetails.getUsername(),start,end));

    }
}
