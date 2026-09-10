package com.bitly.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
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

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/dashboard")
@RestController
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            final DashboardService dashboardService) {
        this.dashboardService = dashboardService;

    }

    @Cacheable(value = "dashboard-summary",key="'dashboard-summary'")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end

    ) {

        return ResponseEntity.ok(dashboardService.getDashboardSummary(userDetails.getUsername(), start, end));
    }

    @Cacheable(value = "dashboard-by-category",key="'dashboard-by-category'")
    @GetMapping("/by-category")
    public ResponseEntity<List<DashboardByCategoryResponse>> getExpensesByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end

    ) {
        return ResponseEntity.ok(dashboardService.getExpensesByCategory(userDetails.getUsername(), start, end));
    }


    @Cacheable(value = "dashboard-monthly-trends",key="'dashboard-monthly-trends'")
    @GetMapping("/monthly-trends")
    public ResponseEntity<List<DashboardTrendsResponse>> getMonthlyTrends(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end

    ) {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends(userDetails.getUsername(), start, end));
    }

    @Cacheable(value = "dashboard-by-account",key="'dashboard-by-account'")
    @GetMapping("/by-account")
    public ResponseEntity<List<DashboardByAccountResponse>> getBalanceByAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime end) {

        return ResponseEntity.ok(dashboardService.getBalanceAccount(userDetails.getUsername(), start, end));

    }
}
