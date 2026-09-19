package com.ptc.halo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "YOUR_SECRET_KEY_MUST_BE_AT_LEAST_32_CHARACTERS_LONG";


    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }


    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + 1000 * 60 * 60 * 24)
                )
                .signWith(getSigningKey())
                .compact();
    }
    public String generateToken(
            String email,
            String sessionId) {

        return Jwts.builder()
                .subject(email)
                .claim("sessionId", sessionId)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + 1000 * 60 * 60 * 24)
                )
                .signWith(getSigningKey())
                .compact();
    }


    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();

    }
    public String extractSessionId(String token) {

        return extractAllClaims(token)
                .get("sessionId", String.class);
    }


    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

}