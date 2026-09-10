package com.bitly.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.dashboard.DashboardByAccountResponse;
import com.bitly.dtos.dashboard.DashboardByCategoryResponse;
import com.bitly.dtos.dashboard.DashboardSummaryResponse;
import com.bitly.dtos.dashboard.DashboardTrendsResponse;
import com.bitly.models.User;
import com.bitly.repository.TransactionRepository;
import com.bitly.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public DashboardService(
            final TransactionRepository transactionRepository,
            final UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private void validateRangeDates(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end are required");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");

        }

        if (start.plusMonths(5).isBefore(end)) {

            throw new IllegalArgumentException("Date range cannot exceed 5 months");
        }

    }

    public DashboardSummaryResponse getDashboardSummary(String username, LocalDateTime start, LocalDateTime end) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Long userId = user.getId();
        validateRangeDates(start, end);

        BigDecimal income = transactionRepository.sumIncome(userId, start, end);
        BigDecimal expense = transactionRepository.sumExpense(userId, start, end);

        BigDecimal balance = income.subtract(expense);
        BigDecimal saveRate = BigDecimal.ZERO;

        if (income.compareTo(BigDecimal.ZERO) > 0) {

            saveRate = balance.divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new DashboardSummaryResponse(
                income,
                expense,
                balance,
                saveRate);

    }

    public List<DashboardByCategoryResponse> getExpensesByCategory(
            String username,
            LocalDateTime start,
            LocalDateTime end

    ) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Long userId = user.getId();
        validateRangeDates(start, end);

        return transactionRepository.getExpensesByCategory(userId, start, end);
    }

    public List<DashboardTrendsResponse> getMonthlyTrends(
            String username,
            LocalDateTime start,
            LocalDateTime end

    ) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Long userId = user.getId();
        validateRangeDates(start, end);

        return transactionRepository.findMonthlyTrends(userId, start, end);
    }

    public List<DashboardByAccountResponse> getBalanceAccount(
            String username,
            LocalDateTime start,
            LocalDateTime end) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Long userId = user.getId();

        return transactionRepository.getBalanceByAccount(userId, start, end);
    }

}
