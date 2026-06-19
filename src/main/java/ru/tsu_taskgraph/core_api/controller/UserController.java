package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.config.OpenApiConfig;
import ru.tsu_taskgraph.core_api.dto.user.SavedAiSettings;
import ru.tsu_taskgraph.core_api.dto.user.UpdateAiSettingsRequest;
import ru.tsu_taskgraph.core_api.dto.user.UpdateProfileRequest;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;
import ru.tsu_taskgraph.core_api.service.FileStorageService;
import ru.tsu_taskgraph.core_api.service.UserService;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Профиль и AI-настройки текущего пользователя")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Профиль текущего пользователя",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль пользователя получен"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public UserProfile getCurrentUser() {
        return userService.getCurrentUserProfile();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Обновить профиль (displayName)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public UserProfile updateCurrentUser(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateCurrentUser(request);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Загрузить / заменить аватар текущего пользователя",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватар успешно загружен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос. Возможные сообщения:\n" +
                    "* 'Файл не выбран'\n" +
                    "* 'Разрешены только изображения'"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера. Возможное сообщение:\n" +
                    "* 'Не удалось сохранить файл'")
    })
    public UserProfile uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Удалить аватар (сбросить на дефолтный)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватар успешно удален"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public UserProfile deleteAvatar() {
        return userService.deleteAvatar();
    }

    @GetMapping(value = "/avatar/{filename:.+}")
    @Operation(summary = "Получить файл аватарки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл найден"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Файл не найден: 123'")
    })
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        FileStorageService.StoredFile storedFile = userService.getAvatar(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storedFile.contentType()))
                .body(storedFile.resource());
    }

    @GetMapping("/ai-settings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получить AI-настройки (ключ замаскирован)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Настройки найдены"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможные сообщения:\n" +
                    "* 'Пользователь с id=123 не найден'\n" +
                    "* 'AI-настройки не найдены'")
    })
    public SavedAiSettings getAiSettings() {
        return userService.getAiSettings();
    }

    @PutMapping("/ai-settings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Сохранить AI-настройки (ключ шифруется AES-256)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Настройки успешно сохранены"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public SavedAiSettings saveAiSettings(@Valid @RequestBody UpdateAiSettingsRequest request) {
        return userService.saveAiSettings(request);
    }

    @DeleteMapping("/ai-settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить AI-настройки и ключ",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Настройки успешно удалены"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации. Возможные сообщения:\n" +
                    "* 'Пользователь не авторизован'\n" +
                    "* 'Principal имеет некорректный тип'"),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден. Возможное сообщение:\n" +
                    "* 'Пользователь с id=123 не найден'")
    })
    public void deleteAiSettings() {
        userService.deleteAiSettings();
    }
}