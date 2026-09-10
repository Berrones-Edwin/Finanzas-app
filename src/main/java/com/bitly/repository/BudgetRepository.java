package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import com.bitly.models.Budget;

import java.time.LocalDate;

public interface BudgetRepository extends JpaRepository<Budget, Long>, JpaSpecificationExecutor<Budget> {

        Optional<Budget> findByIdAndUserEmail(Long id, String username);

        boolean existsByCategoryIdAndUserIdAndMonth(Long categoryId, Long userId, LocalDate month);
}
