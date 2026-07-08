package com.bitly.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserEmail(Long id, String email);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t" +
            " WHERE t.user.id = :userId AND t.category.id = :categoryId " +
            "AND t.transactionType='EXPENSE' AND t.date BETWEEN :start AND :end")
    BigDecimal sumExpenseAmount(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
