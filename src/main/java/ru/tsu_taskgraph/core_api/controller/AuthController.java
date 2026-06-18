package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tsu_taskgraph.core_api.config.OpenApiConfig;
import ru.tsu_taskgraph.core_api.dto.auth.AuthResponse;
import ru.tsu_taskgraph.core_api.dto.auth.LoginRequest;
import ru.tsu_taskgraph.core_api.dto.auth.RefreshRequest;
import ru.tsu_taskgraph.core_api.dto.auth.RegisterRequest;
import ru.tsu_taskgraph.core_api.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Регистрация, вход, выход, токены")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "409", description = "Конфликт. Возможное сообщение:\n" +
                    "* 'Email уже занят'")
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Вход")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вход выполнен успешно"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможное сообщение:\n" +
                    "* 'Неверный email или пароль'")
    })
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить токены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможное сообщение:\n" +
                    "* 'Невалидный refresh token'")
    })
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход", security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", description = "Выход выполнен успешно (refresh-токен удален, если был предоставлен)")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
    }
}