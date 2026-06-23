package com.bitly.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bitly.dtos.AccountBalanceResponse;
import com.bitly.dtos.AccountCreateRequest;
import com.bitly.dtos.AccountResponse;
import com.bitly.dtos.AccountUpdateRequest;
import com.bitly.dtos.PageResponse;
import com.bitly.exceptions.AccountAlreadyExistsException;
import com.bitly.exceptions.AccountConflictException;
import com.bitly.mappers.AccountMapper;
import com.bitly.models.Account;
import com.bitly.models.User;
import com.bitly.repository.AccountRepository;
import com.bitly.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

        private final AccountRepository accountRepository;
        private final UserRepository userRepository;
        private final AccountMapper accountMapper;

        public AccountService(
                        AccountRepository accountRepository,
                        UserRepository userRepository,
                        AccountMapper accountMapper) {
                this.accountRepository = accountRepository;
                this.userRepository = userRepository;
                this.accountMapper = accountMapper;
        }

        @Transactional(readOnly = true)
        public PageResponse<AccountResponse> getAllAccounts(String username, int page, int size,
                        boolean includeInactive) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

                Page<Account> accounts;

                if (includeInactive) {
                        accounts = accountRepository.findByUserId(user.getId(), pageable);
                } else {
                        accounts = accountRepository.findByUserIdAndIsActiveTrue(user.getId(), pageable);
                }

                List<AccountResponse> accountResponses = accounts.stream()
                                .map(accountMapper::toDTO).toList();

                return new PageResponse<>(
                                accountResponses,
                                accounts.getNumber(),
                                accounts.getSize(),
                                accounts.getTotalElements(),
                                accounts.getTotalPages(),
                                accounts.isFirst(),
                                accounts.isLast());
        }

        @Transactional(readOnly = true)
        public AccountResponse getAccountById(String username, long id) {

                AccountResponse account = accountRepository.findByIdAndUserEmail(id, username)
                                .map(accountMapper::toDTO)
                                .orElseThrow(() -> new EntityNotFoundException("No Account found with the id " + id));

                return account;
        }

        @Transactional
        public AccountResponse saveAccount(AccountCreateRequest request, String username) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User was not found wit email " + username));

                boolean isAccountDuplicated = accountRepository.existsByNameIgnoreCaseAndUserId(request.name(),
                                user.getId());

                if (isAccountDuplicated) {
                        throw new AccountAlreadyExistsException(
                                        "You have an account name " + request.name());
                }

                BigDecimal initialBalance = request.balance() != null ? request.balance() : BigDecimal.ZERO;

                Account account = Account.builder()
                                .name(request.name())
                                .accountType(request.accountType())
                                .currency(accountMapper.toCurrency(request.currency()))
                                .color(request.color())
                                .user(user)
                                .balance(initialBalance)
                                .build();

                Account newAccount = accountRepository.save(account);

                return accountMapper.toDTO(newAccount);
        }

        @Transactional
        public AccountResponse updateAccount(AccountUpdateRequest request, String username, long id) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User was not found with email " + username));

                boolean isAccountDuplicated = accountRepository.existsByNameIgnoreCaseAndUserIdAndIdNot(request.name(),
                                user.getId(), id);

                if (isAccountDuplicated) {
                        throw new AccountAlreadyExistsException(
                                        "You have an account name " + request.name());
                }

                Account existAccount = accountRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Account was not found with id " + id));

                existAccount.setName(request.name());
                existAccount.setAccountType(request.accountType());
                existAccount.setCurrency(
                                accountMapper.toCurrency(request.currency()));
                existAccount.setColor(request.color());


                try {
                        
                        Account accountUpdated = accountRepository.saveAndFlush(existAccount);
                        return accountMapper.toDTO(accountUpdated);
                } catch (ObjectOptimisticLockingFailureException ex) {
           
                        throw new AccountConflictException("The account was modified by another operation. please retry.");
                }

        }

        @Transactional
        public void deleteAccount(String username, long id) {

                Account account = accountRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Account was not found with id " + id));

                account.setActive(false);
                accountRepository.delete(account);
        }

        @Transactional(readOnly = true)
        public AccountBalanceResponse getBalanceAccount(String username, long id) {

                var response = accountRepository.getBalance(username, id)
                                .orElseThrow(() -> new EntityNotFoundException("Account not found or unauthorized"));

                        return new AccountBalanceResponse(response.getBalance(), response.getCurrency().getCurrencyCode());

        }

}
