package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.Category;

public interface CategoryRepository extends JpaRepository<Long,Category> {

    Page<Category> findByUserId(Long userId,Pageable pageable);

    Optional<Category> findByIdAndUserId(Long id,Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name,Long userId);
}
