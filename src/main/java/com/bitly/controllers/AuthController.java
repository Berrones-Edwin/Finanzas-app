package com.bitly.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.dtos.AuthenticationRequest;
import com.bitly.dtos.AuthenticationResponse;
import com.bitly.dtos.LogoutRequest;
import com.bitly.dtos.ReqisterRequest;
import com.bitly.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {


    private final AuthService userService;
    public AuthController(AuthService userService){

        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUserEntity(@RequestBody @Valid ReqisterRequest registerRequest) {
        

        userService.registerUser(registerRequest);

        var response =new HashMap<String,String>();
        response.put("ok","true");

        return ResponseEntity.ok(response);
    }
    

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse>login(
        @RequestBody @Valid AuthenticationRequest request 
    ){
        return ResponseEntity.ok(userService.authenticate(request));
    }


    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        
        userService.logout(request);

        var response =new HashMap<String,String>();
        response.put("ok","true");

        return ResponseEntity.ok(response);
    }

}
