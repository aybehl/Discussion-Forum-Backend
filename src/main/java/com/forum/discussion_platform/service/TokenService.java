package com.forum.discussion_platform.service;

import com.forum.discussion_platform.enums.UserRole;
import com.forum.discussion_platform.exception.UnauthorizedAccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String base64SecretKey;

    @Value("${jwt.expiration}")
    private Long expiration; // expiration in milliseconds

    private Key secretKey;

    @PostConstruct
    public void init(){
        byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    // Method to generate token with user details
    public String generateToken(Long userId, UserRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    // Extract user ID from token
    public Long getUserIdFromToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Claims claims = getAllClaimsFromToken(jwtToken);
            return Long.parseLong(claims.get("userId").toString());
        } catch (Exception ex){
            throw new UnauthorizedAccessException("Invalid JWT token - " + ex.getMessage());
        }
    }

    // Extract user role from token
    public UserRole getUserRoleFromToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Claims claims = getAllClaimsFromToken(jwtToken);
            return (UserRole) claims.get("role");
        } catch (Exception ex){
            throw new UnauthorizedAccessException("Invalid JWT token - " + ex.getMessage());
        }
    }

    // Method to check if the token has expired
    public boolean isTokenExpired(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Date expiration = getAllClaimsFromToken(jwtToken).getExpiration();
        return expiration.before(new Date());
    }

    // Method to validate token
    public boolean validateToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // token is invalid
        }
    }

    // Retrieve all claims for a token
    private Claims getAllClaimsFromToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
