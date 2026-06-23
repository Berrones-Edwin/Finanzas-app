package com.bitly.services;

import java.time.LocalDateTime;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.transactions.TransactionAccountResponse;
import com.bitly.dtos.transactions.TransactionCategoryResponse;
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

        Category category = categoryRepository.findByIdAndUserEmail(request.categoryId(),username).orElseThrow(
                () -> new EntityNotFoundException("No category found with the id " + request.categoryId()));

        Account account = accountRepository.findByIdAndUserEmail(request.accountId(),username).orElseThrow(
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

}
