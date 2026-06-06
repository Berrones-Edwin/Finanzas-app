package com.bitly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bitly.models.User;

public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findByUsername(String username);
}
