package com.bitly.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.transactions.TransactionCreateRequest;
import com.bitly.dtos.transactions.TransactionResponse;
import com.bitly.enums.TransactionType;
import com.bitly.exceptions.AccountConflictException;
import com.bitly.exceptions.InsufficientFundsException;
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

public class TransactionService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            TransactionMapper transactionMapper) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public TransactionResponse saveTransaction(TransactionCreateRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

        Category category = categoryRepository.findByIdAndUserEmail(request.categoryId(), username).orElseThrow(
                () -> new EntityNotFoundException("No category found with the id " + request.categoryId()));

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
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new AccountConflictException("The account was modified concurrently, please retry");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .account(account)
                .transactionType(request.transactionType())
                .amount(request.amount())
                .description(request.description())
                .date(LocalDateTime.now())
                .build();

        Transaction transactionSaved = transactionRepository.save(transaction);

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
            LocalDateTime start,
            LocalDateTime end) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

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

        if (start != null || end != null) {

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
