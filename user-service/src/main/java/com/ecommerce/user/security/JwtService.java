package com.ecommerce.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")  private String secret;
    @Value("${jwt.expiry}")  private long   expiryMs;

    public String generate(String subject, Map<String, Object> claims) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder().subject(subject).claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiryMs))
            .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Claims validate(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith((javax.crypto.SecretKey) key)
            .build().parseSignedClaims(token).getPayload();
    }

    public boolean isExpired(String token) {
        try { return validate(token).getExpiration().before(new Date()); }
        catch (JwtException e) { return true; }
    }
}
