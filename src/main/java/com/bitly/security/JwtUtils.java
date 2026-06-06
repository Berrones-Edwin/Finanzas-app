package com.bitly.security;

import java.security.Key;
import java.sql.Date;
import java.util.Base64.Decoder;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import com.bitly.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {


    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpiration;
    public String getJwtFromHeader(HttpServletRequest request){

        String bearerToken = request.getHeader("Authorization");
        if(bearerToken!=null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        return null;
    }

    public String generateToken(UserDetailsImpl userDetails){

        long now = System.currentTimeMillis();
        
        String username = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(","));
        return Jwts.builder()
        .subject(username)
        .claim("roles", roles)
        .issuedAt(new Date(now))
        .expiration(new Date((now + jwtExpiration)))
        .signWith( getKey())
        .compact();
    }

    private Key getKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
        .verifyWith((SecretKey) getKey())
        .build().parseSignedClaims(token)
        .getPayload().getSubject();
    }

    public boolean validateToekn(String authToken){

        try {
            
            Jwts.parser().verifyWith((SecretKey) getKey())
            .build().parseSignedClaims(authToken);
    
            return true;
            
        } catch (IllegalArgumentException e) {

            throw new RuntimeException(e);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }
}
