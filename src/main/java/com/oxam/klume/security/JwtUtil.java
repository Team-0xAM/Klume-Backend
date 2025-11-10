package com.oxam.klume.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/* 설명. JWT 토큰 생성, 검증, 파싱을 담당하는 유틸리티 클래스 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenValidityTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidityTime
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityTime = accessTokenValidityTime;
    }

    /* 설명. Access Token 생성 */
    public String createAccessToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityTime);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    /* 설명. 토큰에서 이메일 추출 */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /* 설명. 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /* 설명. 토큰에서 Claims 추출 */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
