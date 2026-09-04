package com.bitly.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bitly.dtos.AuthenticationRequest;
import com.bitly.dtos.AuthenticationResponse;
import com.bitly.dtos.RefreshTokenRequest;
import com.bitly.dtos.ReqisterRequest;
import com.bitly.enums.CurrencyEnum;
import com.bitly.enums.TokenType;
import com.bitly.exceptions.RefreshTokenException;
import com.bitly.models.Token;
import com.bitly.models.User;
import com.bitly.repository.TokenRepository;
import com.bitly.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        @Transactional
        public void registerUser(ReqisterRequest request) {

                User user = new User();
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setEmail(request.getEmail());
                user.setPassword(
                                passwordEncoder.encode(request.getPassword()));
                user.setUpdatedBy(1L);
                user.setCreatedBy(1L);
                user.setPreferredCurrency(CurrencyEnum.valueOf(request.getPreferredCurrency()));

                userRepository.save(user);

                log.info("User registered successfully. User with id ={}", user.getId());

        }

        @Transactional
        public AuthenticationResponse authenticate(AuthenticationRequest request) {

                var auth = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                var claims = new HashMap<String, Object>();

                var user = (User) auth.getPrincipal();
                claims.put("fullname", user.getFullName());

                return issueTokenPair(user);
        }

        @Transactional
        public AuthenticationResponse refreshToken(RefreshTokenRequest request) {

                String providedRefresh = request.getRefreshToken();

                Token stored = tokenRepository.findByTokenAndTokenType(providedRefresh, TokenType.REFRESH)
                                .orElseThrow(() -> new RefreshTokenException("Invalid refresh token"));

                if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
                        log.warn("Refresh token rejected. tokenId={}, revoked={}, expired={}",
                                        stored.getId(),
                                        stored.isRevoked(),
                                        stored.getExpiresAt().isBefore(LocalDateTime.now()));
                        throw new RefreshTokenException("Refresh token has been revoked or expired");
                }

                User user = stored.getUser();

                if (!jwtService.isRefreshToken(providedRefresh) || !user.getEmail().equals(jwtService.extractUsername(providedRefresh))) {
                        throw new RefreshTokenException("Invalid refresh token");
                }

                if (jwtService.isTokenExpired(providedRefresh)) {
                        throw new RefreshTokenException("Refresh token has expired");
                }

                stored.setRevoked(true);
                stored.setValidatedAt(LocalDateTime.now());
                tokenRepository.save(stored);

                AuthenticationResponse response = issueTokenPair(user);

                log.info("Tokens refreshed. userId={}", user.getId());

                return response;
        }

        private AuthenticationResponse issueTokenPair(User user) {

                var claims = new HashMap<String, Object>();
                claims.put("fullname", user.getFullName());

                String jwtToken = jwtService.generateToken(claims, user);
                String refreshToken = jwtService.generateRefreshToken(claims, user);
                LocalDateTime now = LocalDateTime.now();

                Token tokenEntity = Token.builder()
                                .token(jwtToken)
                                .tokenType(TokenType.ACCESS)
                                .user(user)
                                .createdAt(now)
                                .expiresAt(now.plus(Duration.ofMillis(jwtService.getJwtExpiration())))
                                .revoked(false)
                                .build();

                tokenRepository.save(tokenEntity);

                Token refreshTokenEntity = Token.builder()
                                .token(refreshToken)
                                .tokenType(TokenType.REFRESH)
                                .user(user)
                                .createdAt(now)
                                .expiresAt(now.plus(java.time.Duration.ofMillis(jwtService.getRefreshExpiration())))
                                .revoked(false)
                                .build();

                tokenRepository.save(refreshTokenEntity);

                log.info("Token pair issued. userId={}, accessTokenId={}, refreshTokenId={}",
                                user.getId(), tokenEntity.getId(), refreshTokenEntity.getId());

                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        @Transactional
        public void logout(HttpServletRequest logoutRequest) {

                String authHeader = logoutRequest.getHeader(HttpHeaders.AUTHORIZATION);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {

                        String jwt = authHeader.substring(7);

                        Token token = tokenRepository.findByToken(jwt)
                                        .orElseThrow(() -> new IllegalArgumentException("Token was not found"));

                        token.setRevoked(true);
                        token.setValidatedAt(LocalDateTime.now());

                        log.info("User logged out. userId={}, tokenId={}",
                                        token.getUser().getId(),
                                        token.getId());
                }
        }
}
