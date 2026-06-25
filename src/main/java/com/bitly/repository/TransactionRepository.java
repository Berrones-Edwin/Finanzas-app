package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bitly.models.Transaction;


public interface TransactionRepository extends JpaRepository<Transaction,Long>,JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserEmail(Long id,String email);
}
