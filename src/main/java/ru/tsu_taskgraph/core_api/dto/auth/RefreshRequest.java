package ru.tsu_taskgraph.core_api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
