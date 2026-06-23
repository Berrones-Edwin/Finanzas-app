package com.bitly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

}
