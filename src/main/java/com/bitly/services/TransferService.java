package com.bitly.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bitly.dtos.PageResponse;
import com.bitly.dtos.transfers.TransferCreateRequest;
import com.bitly.dtos.transfers.TransferResponse;
import com.bitly.exceptions.InsufficientFundsException;
import com.bitly.mappers.TransferMapper;
import com.bitly.models.Account;
import com.bitly.models.Transfer;
import com.bitly.models.User;
import com.bitly.repository.AccountRepository;
import com.bitly.repository.TransferRepository;
import com.bitly.repository.UserRepository;
import com.bitly.specifications.TransferSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransferService {

        private final TransferRepository transferRepository;
        private final TransferMapper transferMapper;
        private final UserRepository userRepository;
        private final AccountRepository accountRepository;

        public TransferService(
                        TransferRepository transferRepository,
                        TransferMapper transferMapper,
                        UserRepository userRepository,
                        AccountRepository accountRepository) {
                this.transferRepository = transferRepository;
                this.transferMapper = transferMapper;
                this.userRepository = userRepository;
                this.accountRepository = accountRepository;
        }

        @Transactional(readOnly = true)
        public TransferResponse getTransferById(Long id, String username) {

                Transfer transfer = transferRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Transfer not found with id " + id));

                return transferMapper.toDTO(transfer);
        }

        @Transactional(readOnly = true)
        public PageResponse<TransferResponse> getTransfer(
                        String username,
                        int page,
                        int size,
                        Long accountId,
                        LocalDate start,
                        LocalDate end) {

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

                Specification<Transfer> spec = Specification.where(TransferSpecification.hasUserEmail(username));

                if (accountId != null) {
                        spec = spec.and(TransferSpecification.hasAccountId(accountId));
                }

                if (start != null && end != null) {

                        spec = spec.and(TransferSpecification.betweenDates(start, end));
                }

                Page<Transfer> transferPage = transferRepository.findAll(spec, pageable);

                List<TransferResponse> content = transferPage.stream()
                                .map(transferMapper::toDTO)
                                .toList();

                return new PageResponse<>(
                                content,
                                transferPage.getNumber(),
                                transferPage.getSize(),
                                transferPage.getTotalElements(),
                                transferPage.getTotalPages(),
                                transferPage.isFirst(),
                                transferPage.isLast());
        }

        @Transactional
        public TransferResponse saveTransfer(String username, TransferCreateRequest request) {

                User user = userRepository.findByEmail(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));

                Account fromAccount = accountRepository.findByIdAndUserIdForUpdate(request.fromAccount(), username)
                                .orElseThrow(
                                                () -> new EntityNotFoundException("Origin Account not found with id "
                                                                + request.fromAccount()));

                Account toAccount = accountRepository.findByIdAndUserIdForUpdate(request.toAccount(), username)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Destination Account not found with id " + request.fromAccount()));

                if (fromAccount.getId().equals(toAccount.getId())) {

                        log.warn("Transfer failed. Illegal Arguments. fromAccount={}, toAccount={}, amount={}, userId={}",
                                        fromAccount.getId(),
                                        toAccount.getId(),
                                        request.amount(),
                                        user.getId());
                        throw new IllegalArgumentException("Source and Destination account cannot be the same");
                }

                if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
                        throw new InsufficientFundsException("You do not have enough money in your account");
                }

                fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));

                toAccount.setBalance(toAccount.getBalance().add(request.amount()));

                Transfer transfer = Transfer.builder()
                                .user(user)
                                .fromAccount(fromAccount)
                                .toAccount(toAccount)
                                .amount(request.amount())
                                .description(request.description())
                                .date(request.date())
                                .build();

                Transfer savedTransfer = transferRepository.save(transfer);

                log.info("Transfer completed. fromAccount={}, toAccount={}, amount={}, userId={}",
                                fromAccount.getId(),
                                toAccount.getId(),
                                request.amount(),
                                user.getId());

                return transferMapper.toDTO(savedTransfer);
        }

        @Transactional
        public void deleteTransfer(Long id, String username) {

                Transfer transfer = transferRepository.findByIdAndUserEmail(id, username)
                                .orElseThrow(() -> new EntityNotFoundException("Transfer not found with id " + id));

                BigDecimal amount = transfer.getAmount();
                Long fromAccountId = transfer.getFromAccount().getId();
                Long toAccountId = transfer.getToAccount().getId();

                Account fromAccount;
                Account toAccount;

                if (fromAccountId < toAccountId) {
                        fromAccount = accountRepository.findByIdAndUserIdForUpdate(fromAccountId, username)
                                        .orElseThrow(
                                                        () -> new EntityNotFoundException(
                                                                        "Origin Account not found with id "
                                                                                        + fromAccountId));
                        toAccount = accountRepository.findByIdAndUserIdForUpdate(toAccountId, username)
                                        .orElseThrow(
                                                        () -> new EntityNotFoundException(
                                                                        "Destination Account not found with id "
                                                                                        + toAccountId));
                } else {
                        toAccount = accountRepository.findByIdAndUserIdForUpdate(toAccountId, username)
                                        .orElseThrow(
                                                        () -> new EntityNotFoundException(
                                                                        "Destination Account not found with id "
                                                                                        + toAccountId));
                        fromAccount = accountRepository.findByIdAndUserIdForUpdate(fromAccountId, username)
                                        .orElseThrow(
                                                        () -> new EntityNotFoundException(
                                                                        "Origin Account not found with id "
                                                                                        + fromAccountId));
                }

                fromAccount.setBalance(fromAccount.getBalance().add(amount));

                toAccount.setBalance(toAccount.getBalance().subtract(amount));

                transferRepository.delete(transfer);

        }

}
