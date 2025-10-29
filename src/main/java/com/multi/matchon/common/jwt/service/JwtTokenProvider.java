package com.multi.matchon.common.jwt.service;

import com.multi.matchon.member.domain.MemberRole;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;
    private final long accessTokenValidity = 60 * 60 * 1000L; // 1시간
    private final long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000L; // 14일

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.issuer}") String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
    }

    public String createAccessToken(String email, MemberRole role) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String email, MemberRole role) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .claim("role", role.name()) // role 포함해야 getRoleFromToken이 null 안 됨
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public MemberRole getRoleFromToken(String token) {
        String role = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        if (role == null) {
            throw new IllegalArgumentException("JWT 토큰에 role 정보가 없습니다.");
        }

        return MemberRole.valueOf(role);
    }
}

