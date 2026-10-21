package com.bitly.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bitly.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${refresh.expiration}")
    private long jwtRefreshExpiration;

    private static final String SESSION_ID_CLAIM = "sid";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    public boolean isRefreshToken(String token) {
        return TokenType.REFRESH.name().equals(extractTokenType(token));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {

        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails, String sessionId) {
        return buildToken(claims, userDetails, jwtExpiration, TokenType.ACCESS, sessionId);
    }

    public String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails, String sessionId) {
        return buildToken(claims, userDetails, jwtRefreshExpiration, TokenType.REFRESH, sessionId);
    }

    public String buildToken(
            Map<String, Object> claims,
            UserDetails userDetails,
            long jwtExpiration,
            TokenType tokenType,
            String sessionId

    ) {

        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
                .claim(SESSION_ID_CLAIM, sessionId)
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    public long getRefreshExpiration() {
        return jwtRefreshExpiration;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isAccessToken(String token) {
        return TokenType.ACCESS.name().equals(extractTokenType(token));
    }

    public String extractSessionId(String token) {
        return extractClaim(token, claims -> claims.get(SESSION_ID_CLAIM, String.class));
    }

}
