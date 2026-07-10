package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bitly.models.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    Page<Transfer> findByUserId(Long UserId, Pageable pageable);

    @Query("SELECT t FROM Transfer t JOIN FETCH t.fromAccount JOIN FETCH t.toAccount WHERE t.id = :id AND t.user.email =:email")
    Optional<Transfer> findByIdAndUserEmail(
            @Param("id") Long id,
            @Param("email") String email);
}
