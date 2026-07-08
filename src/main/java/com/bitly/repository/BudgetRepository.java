package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.models.Budget;

import java.time.LocalDate;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserEmail(Long id, String username);

    boolean existsByCategoryIdAndUserIdAndMonth(Long categoryId, Long userId, LocalDate month);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND YEAR(b.month)= :year AND MONTH(b.MONTH) = :month")
    Page<Budget> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable

    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND YEAR(b.month) = :year")
    Page<Budget> findByUserIdAndYear(
            @Param("userId") Long userId,
            @Param("year") Integer year,
            Pageable pageable);
}
