package com.job.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {


    @Value("${jwt.secret:yourSuperLongSecretKeyThatIsAtLeast512BitsLongForHS512Algorithm}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationInMs;

    private SecretKey getSigningKey() {
        // 如果密钥长度足够，直接使用
        if (jwtSecret != null && jwtSecret.length() >= 64) { // 至少64个字符（512位）
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } else {
            // 如果密钥太短，生成一个安全的密钥（仅用于开发环境）
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal);
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .claim("role", userPrincipal.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            // 无效的JWT签名
            log.info("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            // 无效的JWT令牌
            log.info("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            // 过期的JWT令牌
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            // 不支持的JWT令牌
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            // JWT声明字符串为空
            log.info("JWT claims string is empty.");
        }
        return false;
    }

    public static void main(String[] args) {

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated secure JWT secret: " + base64Key);
        System.out.println("Key length: " + base64Key.length() + " characters");
        System.out.println("Key bits: " + key.getEncoded().length * 8 + " bits");
    }
}