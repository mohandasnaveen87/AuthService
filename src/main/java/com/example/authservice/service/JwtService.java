package com.example.authservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.example.authservice.RoleNotFoundException;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // A secure signing key (In production, load this securely from an environment variable!)
//    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//
//    public String generateToken(String username, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", role); // e.g., "ROLE_USER" or "ROLE_ADMIN"
//        
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                // Token valid for 24 hours
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) 
//                .signWith(SECRET_KEY)
//                .compact();
//    }
	private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(String username, String role){
        Instant now = Instant.now();
        
        // Define your scopes and metadata using Spring Security's JwtClaimsSet DSL
        if (!role.equalsIgnoreCase("PARENT") && !role.equalsIgnoreCase("CHILD")) {
            throw new RoleNotFoundException("You are not authorized.");
        }
        
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(86400)) // 24 Hours
                .subject(username)
                .claim("scope", role) // Spring Security expects roles mapped into a "scope" or "scp" claim
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
        
       
}