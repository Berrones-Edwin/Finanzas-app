package com.bitly.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.transactions.TransactionCreateRequest;
import com.bitly.dtos.transactions.TransactionResponse;
import com.bitly.enums.TransactionType;
import com.bitly.exceptions.AccountConflictException;
import com.bitly.exceptions.InsufficientFundsException;
import com.bitly.exceptions.TransactionTypeMismatchException;
import com.bitly.mappers.TransactionMapper;
import com.bitly.models.Category;
import com.bitly.models.Transaction;
import com.bitly.models.User;
import com.bitly.models.Account;
import com.bitly.repository.AccountRepository;
import com.bitly.repository.CategoryRepository;
import com.bitly.repository.TransactionRepository;
import com.bitly.repository.UserRepository;
import com.bitly.specifications.TransactionSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final BudgetAlertService budgetAlertService;

    public TransactionService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            BudgetAlertService budgetAlertService, BudgetService budgetService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.budgetAlertService = budgetAlertService;
    }

    @Transactional
    public TransactionResponse saveTransaction(TransactionCreateRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Category category = categoryRepository.findByIdAndUserEmail(request.categoryId(), username).orElseThrow(
                () -> new EntityNotFoundException("No category found with the id " + request.categoryId()));

        if (!category.getCategoryType().name().equals(request.transactionType().name())) {
            throw new TransactionTypeMismatchException(
                    "Transaction type " + request.transactionType()
                            + " does not match category type " + category.getCategoryType());
        }

        Account account = accountRepository.findByIdAndUserEmail(request.accountId(), username).orElseThrow(
                () -> new EntityNotFoundException("No Account found with the id " + request.categoryId()));

        if (request.transactionType() == TransactionType.EXPENSE) {

            if (account.getBalance().compareTo(request.amount()) < 0) {
                throw new InsufficientFundsException("You do not have enough money in your account");
            } else {

                account.setBalance(account.getBalance().subtract(request.amount()));
            }

        } else {

            account.setBalance(account.getBalance().add(request.amount()));
        }

        try {
            accountRepository.saveAndFlush(account);
            log.info(
                    "Account modified. userId={},  type={}, amount={}, accountId={}, categoryId={}",
                    user.getId(),
                    request.transactionType(),
                    request.amount(),
                    account.getId(),
                    category.getId());
        } catch (ObjectOptimisticLockingFailureException ex) {

            log.warn(
                    "Optimistic locking conflict. userId={},  type={}, amount={}, accountId={}, categoryId={}",
                    user.getId(),
                    request.transactionType(),
                    request.amount(),
                    account.getId(),
                    category.getId());

            throw new AccountConflictException("The account was modified concurrently, please retry");
        }

        LocalDateTime ocurredAt = request.date() != null
                ? request.date()
                : LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .account(account)
                .transactionType(request.transactionType())
                .amount(request.amount())
                .description(request.description())
                .date(ocurredAt)
                .build();

        Transaction transactionSaved = transactionRepository.saveAndFlush(transaction);
        budgetAlertService.evaluate(transactionSaved);

        return transactionMapper.toDTO(transactionSaved);

    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getTransactions(
            String username,
            int page,
            int size,
            TransactionType type,
            Long accountId,
            Long categoryId,
            LocalDate start,
            LocalDate end) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Specification<Transaction> spec = Specification.where(TransactionSpecification.hasUserEmail(username));

        if (type != null) {
            spec = spec.and(TransactionSpecification.hasType(type));
        }
        if (accountId != null) {
            spec = spec.and(TransactionSpecification.hasAccountId(accountId));
        }
        if (categoryId != null) {
            spec = spec.and(TransactionSpecification.hasCategorytId(categoryId));
        }

        if (start != null && end != null) {

            spec = spec.and(TransactionSpecification.betweenDates(start, end));
        }

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionResponse> content = transactionPage.stream()
                .map(transactionMapper::toDTO)
                .toList();

        return new PageResponse<>(
                content,
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isFirst(),
                transactionPage.isLast());

    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(String username, Long id) {

        Transaction transaction = transactionRepository.findByIdAndUserEmail(id, username)
                .orElseThrow(() -> new EntityNotFoundException("No Transaction found with the id " + id));

        return transactionMapper.toDTO(transaction);
    }
}
