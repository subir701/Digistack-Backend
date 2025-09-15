package com.digistackBackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signKey;

    public JwtService(@Value("${jwt.secert.key}") String secertKey){
        byte[] decodedKey = Base64.getDecoder().decode(secertKey);
        this.signKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String username, String role){
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("role",role);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3600*1000))
                .and()
                .signWith(signKey)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(signKey).build().parseSignedClaims(token).getPayload();
    }

    public String extractRole(String token) {
        String role = Jwts.parser()
                .verifyWith(signKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // Extract role from token

        // Add the "ROLE_" prefix if it is not already present
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return role;
    }
}
