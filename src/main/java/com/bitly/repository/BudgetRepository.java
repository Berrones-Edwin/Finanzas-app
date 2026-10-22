package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.models.Budget;

import java.time.LocalDate;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

        Optional<Budget> findByIdAndUserEmail(Long id, String username);

        boolean existsByCategoryIdAndUserIdAndMonth(Long categoryId, Long userId, LocalDate month);

        Optional<Budget> findByUserIdAndCategoryIdAndMonth(Long userId, Long categoryId, LocalDate month);

        @Modifying(flushAutomatically = true)
        @Query("""
                                UPDATE Budget b
                                SET b.isAlertSent =true
                                WHERE b.id=:id
                                AND (b.isAlertSent = false OR b.isAlertSent IS NULL)
                                AND b.deletedAt IS NULL
                        """)
        int markAlertSent(@Param("id") Long id);
}
