package com.bitly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.Category;

public interface CategoryRepository extends JpaRepository<Long,Category> {

    List<Category> findByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id,Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name,Long userId);
}
