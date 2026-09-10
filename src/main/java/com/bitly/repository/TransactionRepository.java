package com.bitly.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.dtos.dashboard.DashboardByAccountResponse;
import com.bitly.dtos.dashboard.DashboardByCategoryResponse;
import com.bitly.dtos.dashboard.DashboardTrendsResponse;
import com.bitly.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

        Optional<Transaction> findByIdAndUserEmail(Long id, String email);

        @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t" +
                        " WHERE t.user.id = :userId AND t.category.id = :categoryId " +
                        "AND t.transactionType='EXPENSE' AND t.date BETWEEN :start AND :end")
        BigDecimal sumExpenseAmount(
                        @Param("userId") Long userId,
                        @Param("categoryId") Long categoryId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("""
                        SELECT COALESCE(SUM(t.amount),0)
                        FROM Transaction t
                        WHERE t.user.id = :userId
                        AND t.transactionType ='INCOME'
                        AND t.date BETWEEN :start AND :end
                                """)
        BigDecimal sumIncome(
                        @Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("""
                        SELECT COALESCE(SUM(t.amount),0)
                        FROM Transaction t
                        WHERE t.user.id = :userId
                        AND t.transactionType ='EXPENSE'
                        AND t.date BETWEEN :start AND :end
                                """)
        BigDecimal sumExpense(
                        @Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("""
                        SELECT
                                c.id,
                                c.name,
                                COALESCE(SUM(t.amount),0)
                        FROM Transaction t
                        JOIN t.category c
                        WHERE t.user.id = :userId
                        AND t.transactionType = 'EXPENSE'
                        AND t.date BETWEEN :start AND :end
                        GROUP BY c.id, c.name
                        ORDER BY SUM(t.amount) DESC
                                """)
        List<DashboardByCategoryResponse> getExpensesByCategory(
                        @Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("""
                        SELECT
                        a.id,
                        a.name,
                        COALESCE(SUM(
                                CASE
                                WHEN t.transactionType ='INCOME'
                                THEN t.amount
                                ELSE -t.amount

                                END
                        ),0)
                        FROM Transaction t
                        JOIN t.account a
                        WHERE t.user.id= :userId
                        AND t.date BETWEEN :start AND :end
                        GROUP BY a.id,a.name
                                """)
        List<DashboardByAccountResponse> getBalanceByAccount(
                        @Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

        @Query("""
                        SELECT
                        YEAR(t.date), 
                        MONTH(t.date),
                        COALESCE(
                                SUM(CASE
                                WHEN t.transactionType ='INCOME'
                                THEN t.amount
                                ELSE 0
                                END
                                ),0) AS income,
                        COALESCE(
                                SUM(CASE
                                WHEN t.transactionType ='EXPENSE'
                                THEN t.amount
                                ELSE 0
                                END
                                ),0) AS expense

                        FROM Transaction t
                        WHERE t.user.id = :userId
                        AND t.date BETWEEN :start AND :end
                        GROUP BY YEAR(t.date), MONTH(t.date)
                        ORDER BY YEAR(t.date), MONTH(t.date)
                        """)
        List<DashboardTrendsResponse> findMonthlyTrends(
                        @Param("userId") Long userId,
                        @Param("start") LocalDate start,
                        @Param("end") LocalDate end);

}
