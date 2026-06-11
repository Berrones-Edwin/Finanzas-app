package com.bitly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.Account;

public interface AccountRepository  extends JpaRepository<Long,Account>{

    List<Account> findByUserId(Long userId);

    List<Account> findByUserIdAndIsActiveTrue(Long userId);

    Optional<Account> findByIdAndUserId(Long id,Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name,Long userId);
    

}
