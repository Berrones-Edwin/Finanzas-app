package com.bitly.services;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bitly.dtos.AuthenticationRequest;
import com.bitly.dtos.AuthenticationResponse;
import com.bitly.dtos.ReqisterRequest;
import com.bitly.models.Token;
import com.bitly.models.User;
import com.bitly.repository.TokenRepository;
import com.bitly.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    public AuthService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
        TokenRepository tokenRepository) {

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;

    }

    public void registerUser(ReqisterRequest request) {

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())                
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var claims = new HashMap<String, Object>();

        var user = (User) auth.getPrincipal();
        claims.put("fullname", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);

        Token tokenEntity = Token.builder()
                .token(jwtToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        tokenRepository.save(tokenEntity);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public void logout(HttpServletRequest  logoutRequest){


        String authHeader = logoutRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            
            String jwt = authHeader.substring(7);

            Token token = tokenRepository.findByToken(jwt)
            .orElseThrow(()->new IllegalArgumentException("Token was not found"));
    
            token.setRevoked(true);
            token.setValidatedAt(LocalDateTime.now());
        }
    }
}
