package com.bitly.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

}
