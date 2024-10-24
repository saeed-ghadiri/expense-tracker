package com.sp.expensetracker.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 10; // 10 hours
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 10; // 10 days
    private final String jwtSecret = "uX7@L3q2#nK9p4Z!$wR$8T5v1^Yj0bW&uX7@L3q";
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser() // Updated to use parserBuilder
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // validate both access & refresh token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}

