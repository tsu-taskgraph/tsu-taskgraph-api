package ru.tsu_taskgraph.core_api.dto.auth;


public record AuthResponse(
        String accessToken,
        String refreshToken
        // TODO UserProfile user
) {
}