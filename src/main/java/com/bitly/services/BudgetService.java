package com.bitly.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.budgets.BudgetCreateRequest;
import com.bitly.dtos.budgets.BudgetResponse;
import com.bitly.dtos.budgets.BudgetUpdateRequest;
import com.bitly.exceptions.BudgetAlreadyExistsException;
import com.bitly.mappers.BudgetMapper;
import com.bitly.models.Budget;
import com.bitly.models.Category;
import com.bitly.models.User;
import com.bitly.repository.BudgetRepository;
import com.bitly.repository.CategoryRepository;
import com.bitly.repository.TransactionRepository;
import com.bitly.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BudgetService {

        private final BudgetRepository budgetRepository;
        private final TransactionRepository transactionRepository;
        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;
        private final BudgetMapper budgetMapper;

        public BudgetService(
                        BudgetRepository budgetRepository,
                        TransactionRepository transactionRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        BudgetMapper budgetMapper) {
                this.budgetRepository = budgetRepository;
                this.transactionRepository = transactionRepository;
                this.userRepository = userRepository;
                this.categoryRepository = categoryRepository;
                this.budgetMapper = budgetMapper;
        }

        private BigDecimal getSumExpenseAmount(Budget budget) {

                LocalDate start = budget.getMonth().withDayOfMonth(1);
                LocalDate end = budget.getMonth().withDayOfMonth(
                                budget.getMonth().lengthOfMonth());

                BigDecimal spent = transactionRepository.sumExpenseAmount(
                                budget.getUser().getId(),
                                budget.getCategory().getId(),
                                start,
                                end);
                return spent;
        }

        @Transactional(readOnly = true)
        public BudgetResponse getBudgetById(Long id, String username) {

                Budget budget = budgetRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id " + id));

                return budgetMapper.toDTO(budget, getSumExpenseAmount(budget));

        }

        @Transactional(readOnly = true)
        public PageResponse<BudgetResponse> getAllBudgetsByMonthAndYear(
                        String username,
                        Integer year,
                        Integer month,
                        int page,
                        int size) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

                Page<Budget> budgets = month == null
                                ? budgetRepository.findByUserIdAndYear(user.getId(), year, pageable)
                                : budgetRepository.findByUserIdAndYearAndMonth(user.getId(), year, month,
                                                pageable);

                List<BudgetResponse> budgetResponses = budgets.stream()
                                .map(b -> budgetMapper.toDTO(b, getSumExpenseAmount(b)))
                                .toList();

                return new PageResponse<>(
                                budgetResponses,
                                budgets.getNumber(),
                                budgets.getSize(),
                                budgets.getTotalElements(),
                                budgets.getTotalPages(),
                                budgets.isFirst(),
                                budgets.isLast());
        }

        @Transactional
        public BudgetResponse saveBudget(String username, BudgetCreateRequest request) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Category category = categoryRepository.findByIdAndUserEmail(request.categoryId(), username)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Category not found with id " + request.categoryId()));

                LocalDate month = request.month().withDayOfMonth(1);

                boolean isDuplicated = budgetRepository.existsByCategoryIdAndUserIdAndMonth(request.categoryId(),
                                user.getId(),
                                month);

                if (isDuplicated) {
                        throw new BudgetAlreadyExistsException(
                                        "You have a budget for " + category.getName() + " in " + month.getMonth());
                }

                Integer threShold = request.alertThreshold() != null
                                ? request.alertThreshold()
                                : 80;

                Budget budget = Budget.builder()
                                .user(user)
                                .category(category)
                                .month(month)
                                .amount(request.amount())
                                .alertThreshold(threShold)
                                .notes(request.notes())
                                .build();

                Budget saved = budgetRepository.save(budget);
                return budgetMapper.toDTO(saved, BigDecimal.ZERO);
        }

        @Transactional
        public BudgetResponse updateBudget(String username, BudgetUpdateRequest request,Long id ) {

                Budget budget = budgetRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id " + id));

                budget.setAmount(request.amount());

                if (request.alertThreshold() != null) {
                        budget.setAlertThreshold(request.alertThreshold());
                }

                budget.setNotes(request.notes());

                Budget updated = budgetRepository.save(budget);


                return budgetMapper.toDTO(updated, getSumExpenseAmount(budget));
        }

        @Transactional
        public void deleteBudget(String username, Long id) {

                Budget budget = budgetRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id " + id));

                budgetRepository.delete(budget);

        }
}
