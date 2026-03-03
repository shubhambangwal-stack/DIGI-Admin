package com.vunum.SocietyAdmin.Utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenUtil {
    @Value("${secret.key}")
    private String secretKey;


    public String generateToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
//        long TOKEN_VALIDITY = 1000 * 60 * 60 * 10;
        return Jwts.builder().subject(username)
                .claim("role", role)
                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }


    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)));
//                && !isTokenExpired(token));
    }


    public Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
