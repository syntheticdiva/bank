package com.java.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Сервис для работы с JWT-токенами.
 * <p>
 * Обеспечивает генерацию, валидацию и парсинг JWT-токенов для аутентификации и авторизации пользователей.
 * </p>
 *
 * @author AlinaSheveleva
 * @version 1.0
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    /**
     * Генерирует секретный ключ для подписи токена.
     * <p>
     * Преобразует строку из настроек в ключ.
     * Требует минимальную длину секрета 32 символа (256 бит) для безопасности.
     * </p>
     *
     * @return секретный ключ
     * @throws IllegalArgumentException если секретная строка короче 32 символов
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 characters)");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey())
                .compact();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    /**
     * Извлекает все claims (утверждения) из токена.
     *
     * @param token JWT-токен
     * @return объект claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Извлекает email пользователя из токена.
     *
     * @param token JWT-токен
     * @return email пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает срок действия токена.
     *
     * @param token JWT-токен
     * @return дата истечения срока
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    /**
     * Извлекает конкретное claim-утверждение из токена.
     *
     * @param token JWT-токен
     * @param claimsResolver функция-резолвер для извлечения claim
     * @return значение claim
     * @param <T> тип возвращаемого значения
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

public Boolean validateToken(String token) {
    try {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
        return true;
    } catch (Exception ex) {
        return false;
    }
}
}