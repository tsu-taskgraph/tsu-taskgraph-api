package ru.tsu_taskgraph.core_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    @Getter
    private final long accessTokenExpiration;
    @Getter
    private final long refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.access}") long accessTokenExpiration,
            @Value("${jwt.expiration.refresh}") long refreshTokenExpiration
    ) {
        // Декодируем Base64 строку и создаем криптографически стойкий ключ
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(String email, UUID userId) {
        Date now = new Date();
        Date expirationAt = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(email) // subject обычно используется для хранения логина/email
                .claim("userId", userId.toString()) // Кастомные данные
                .issuedAt(now)
                .expiration(expirationAt)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expirationAt = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expirationAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Проверка валидности токена (не истек ли он, правильная ли подпись).
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }


    public Optional<String> getEmailFromToken(String token) {
        return Optional.ofNullable(getClaims(token).getSubject());
    }

    public Optional<String> getEmailFromAuthHeader(String authHeader) {
        return getTokenFromAuthHeader(authHeader)
                .flatMap(this::getEmailFromToken);
    }

    public Optional<UUID> getUserIdFromToken(String token) {
        return Optional.ofNullable(getClaims(token).get("userId", String.class))
                .map(UUID::fromString);
    }

    public Optional<UUID> getUserIdFromAuthHeader(String authHeader) {
        return getTokenFromAuthHeader(authHeader)
                .flatMap(this::getUserIdFromToken);
    }


    public Optional<String> getTokenFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return Optional.empty();
        else
            return Optional.of(authHeader.substring(7));
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}