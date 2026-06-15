package ru.tsu_taskgraph.core_api.dto.auth;

import ru.tsu_taskgraph.core_api.dto.user.UserProfile;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfile user
) {
}