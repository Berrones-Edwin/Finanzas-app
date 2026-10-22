package com.bitly.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.enums.NotificationStatus;
import com.bitly.enums.NotificationType;
import com.bitly.enums.TransactionType;
import com.bitly.models.Budget;
import com.bitly.models.Notification;
import com.bitly.models.Transaction;
import com.bitly.repository.BudgetRepository;
import com.bitly.repository.NotificationRepository;
import com.bitly.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BudgetAlertService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final HelperService helperService;

    public BudgetAlertService(
            final BudgetRepository budgetRepository,
            final TransactionRepository transactionRepository,
            final HelperService helperService,
            NotificationRepository notificationRepository) {

        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.helperService = helperService;
        this.notificationRepository = notificationRepository;
    }

    @Transactional 
    public void evaluate(Transaction transaction) {

        if (transaction.getTransactionType() != TransactionType.EXPENSE) {

            return;
        }
        LocalDate month = transaction.getDate().toLocalDate().withDayOfMonth(1);

        Long userId = transaction.getUser().getId();
        Long categoryId = transaction.getCategory().getId();
        budgetRepository.findByUserIdAndCategoryIdAndMonth(
                userId,
                categoryId,
                month)
                .ifPresent(budget -> evaluateBudget(budget));
    }

    @Transactional
    public boolean evaluate(Budget budget) {

        if (budget == null || budget.getId() == null) {
            return false;
        }

        return evaluateBudget(budget);
    }

    private boolean evaluateBudget(Budget budget) {

        if (Boolean.TRUE.equals(budget.getIsAlertSent())) {
            return false;
        }

        Long userId = budget.getUser().getId();
        Long categoryId = budget.getCategory().getId();
        LocalDateTime start = helperService.startOfMonth(budget.getMonth());
        LocalDateTime end = helperService.endOfMonth(budget.getMonth());

        BigDecimal spent = transactionRepository.sumExpenseAmount(userId, categoryId, start, end);

        int threshold = budget.getAlertThreshold() != null ? budget.getAlertThreshold() : 80;
        BigDecimal thresholdAmount = budget.getAmount()
                .multiply(BigDecimal.valueOf(threshold))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (spent.compareTo(thresholdAmount) < 0) {
            return false;
        }

        if (budgetRepository.markAlertSent(budget.getId()) == 0) {

            return false;
        }

        log.warn("Budget threshold reached. userId={}, budgetId={}, categoryId={},spent={},amount={}, threshold={}",
                userId,
                budget.getId(),
                categoryId,
                spent,
                budget.getAmount(),
                threshold);

        notificationRepository.save(Notification.builder()
                .user(budget.getUser())
                .budget(budget)
                .type(NotificationType.BUDGET_THRESHOLD_REACHED)
                .message(buildMessage(budget, spent, threshold))
                .status(NotificationStatus.PENDING)
                .attempts(0)
                .build());

        return true;
    }

    private String buildMessage(Budget budget, BigDecimal spent, int threshold) {

        BigDecimal usedPercentage = budget.getAmount().compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : spent.multiply(BigDecimal.valueOf(100))
                        .divide(budget.getAmount(), 1, RoundingMode.HALF_UP);
        String currency = budget.getUser().getPreferredCurrency() != null
                ? budget.getUser().getPreferredCurrency().name()
                : "";
        return String.format(
                "Budget alert: %s is at %s%% (threshold %d%%). Spent %s of %s %s",
                budget.getCategory().getName(),
                usedPercentage.toPlainString(),
                threshold,
                spent.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                budget.getAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                currency);
    }

}
