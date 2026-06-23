package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByUserId(Long userId, Pageable pageable);

    Page<Account> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    Optional<Account> findByIdAndUserEmail(Long id, String email);

    @Query("SELECT a FROM Account a WHERE a.id = :id AND a.user.email = :email")
    Optional<Account> getBalance(@Param("email") String email, @Param("id") long id);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Long userId, Long id);

}
