package com.bitly.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.UserResponse;
import com.bitly.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/users")
@RestController
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    public UserController(
            final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserInformation(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(userService.getInfo(userDetails.getUsername()));

    }

}
