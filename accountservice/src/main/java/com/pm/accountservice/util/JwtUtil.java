package com.pm.accountservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

// este tag registra como una clase Bean a esta clase y spring sabe como inyectar las dependencias en las otras clases
@Component
public class JwtUtil {

    private final Key secretKey;

    // injecting secret key into env variable
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, UserRoles role, Long tenantId) {
        return Jwts.builder()
                .subject(email)
                .claim("tenantId", tenantId)
                .claim("role", role.getRoleName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //10 hours
                .signWith(secretKey)
                .compact();
    }

    public List<String> getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("role", String.class);

        // returns as a list due to spring handling authorities in lists
        return List.of(role);
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getTenantIdFromToken(String token) {
        Claims claims =  Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("tenantId", Long.class);
    }


    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token");
        }
    }

    public boolean isTokenValid(String token) {
        try {
            this.validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
