package com.duoc.semana2.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final Key key;
    private final long jwtExpirationInMillis;

    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMillis = expirationMinutes * 60000;
    }
    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(jwtExpirationInMillis)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token){
        return parseAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
      if (username == null) return false;
        try {
          Claims claims = parseAllClaims(token);
          return username.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
          return false;
        }
    }

    private Claims parseAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
    
}
