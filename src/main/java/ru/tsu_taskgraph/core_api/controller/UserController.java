package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.config.OpenApiConfig;
import ru.tsu_taskgraph.core_api.dto.user.SavedAiSettings;
import ru.tsu_taskgraph.core_api.dto.user.UpdateAiSettingsRequest;
import ru.tsu_taskgraph.core_api.dto.user.UpdateProfileRequest;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;
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
    public UserProfile getCurrentUser() {
        return userService.getCurrentUserProfile();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Обновить профиль (displayName)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public UserProfile updateCurrentUser(@Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateCurrentUser(request);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Загрузить / заменить аватар текущего пользователя",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public UserProfile uploadAvatar(@RequestParam("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @DeleteMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Удалить аватар (сбросить на дефолтный)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public UserProfile deleteAvatar() {
        return userService.deleteAvatar();
    }

    @GetMapping(value = "/avatar/{filename}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить файл аватарки")
    public Resource getAvatar(@PathVariable String filename) {
        return userService.getAvatar(filename);
    }

    @GetMapping("/ai-settings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Получить AI-настройки (ключ замаскирован)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public SavedAiSettings getAiSettings() {
        return userService.getAiSettings();
    }

    @PutMapping("/ai-settings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Сохранить AI-настройки (ключ шифруется AES-256)",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public SavedAiSettings saveAiSettings(@Valid @RequestBody UpdateAiSettingsRequest request) {
        return userService.saveAiSettings(request);
    }

    @DeleteMapping("/ai-settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить AI-настройки и ключ",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    public void deleteAiSettings() {
        userService.deleteAiSettings();
    }
}
