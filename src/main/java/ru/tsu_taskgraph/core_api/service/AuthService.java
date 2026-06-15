package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.auth.AuthResponse;
import ru.tsu_taskgraph.core_api.dto.auth.LoginRequest;
import ru.tsu_taskgraph.core_api.dto.auth.RegisterRequest;
import ru.tsu_taskgraph.core_api.entity.RefreshToken;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.AuthenticationException;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.exception.ResourceConflictException;
import ru.tsu_taskgraph.core_api.repository.RefreshTokenRepository;
import ru.tsu_taskgraph.core_api.repository.UserRepository;
import ru.tsu_taskgraph.core_api.mapper.UserMapper;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceConflictException("Email уже занят");
        }

        var user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .build();

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Неверный email или пароль"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Неверный email или пароль");
        }

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                // удаляем если не валидный
                .map(this::verifyRefreshToken)
                // раскрываем optional
                .map(RefreshToken::getUser)
                // подставляем user
                .map(this::generateAuthResponse)
                .orElseThrow(() -> new AuthenticationException("Невалидный refresh token"));
    }

    @Transactional
    public void logout(String reqRefreshToken) {
        refreshTokenRepository.findByToken(reqRefreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    // --- Вспомогательные методы ---

    private AuthResponse generateAuthResponse(User user) {
        var accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getId());
        var refreshTokenStr = jwtProvider.generateRefreshToken(user.getEmail());

        // Удаляем старый refresh token из БД, если он был (1 сессия на юзера)
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        // Сохраняем новый refresh token
        var refreshTokenObj = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(Instant.now().plusMillis(jwtProvider.getRefreshTokenExpiration()))
                .build();
        refreshTokenRepository.save(refreshTokenObj);

        var userProfile = userMapper.toUserProfile(user);

        return new AuthResponse(accessToken, refreshTokenStr, userProfile);
    }

    private RefreshToken verifyRefreshToken(RefreshToken token) {
        if (!jwtProvider.validateToken(token.getToken())) {
            refreshTokenRepository.delete(token);
            throw new AuthenticationException("Невалидный refresh token");
        }
        return token;
    }
}