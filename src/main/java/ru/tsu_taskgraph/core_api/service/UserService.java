package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.dto.user.SavedAiSettings;
import ru.tsu_taskgraph.core_api.dto.user.UpdateAiSettingsRequest;
import ru.tsu_taskgraph.core_api.dto.user.UpdateProfileRequest;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;
import ru.tsu_taskgraph.core_api.entity.AiSettings;
import ru.tsu_taskgraph.core_api.entity.ProviderSettings;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.AuthenticationException;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.mapper.UserMapper;
import ru.tsu_taskgraph.core_api.repository.UserRepository;
import ru.tsu_taskgraph.core_api.util.UserUtil;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodName;
import ru.tsu_taskgraph.core_api.controller.UserController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUtil userUtil;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public UserProfile getCurrentUserProfile() {
        User dbUser = userUtil.getCurrentUserFromDb();
        return userMapper.toUserProfile(dbUser);
    }

    @Transactional
    public UserProfile updateCurrentUser(UpdateProfileRequest request) {
        User dbUser = userUtil.getCurrentUserFromDb();

        dbUser.setDisplayName(request.getDisplayName());
        userRepository.save(dbUser);

        return userMapper.toUserProfile(dbUser);
    }

    @Transactional
    public UserProfile uploadAvatar(MultipartFile file) {
        User dbUser = userUtil.getCurrentUserFromDb();

        String currentAvatarFilename = null;
        if (dbUser.getAvatarUrl() != null && !dbUser.getAvatarUrl().isEmpty()) {
            currentAvatarFilename = dbUser.getAvatarUrl().substring(dbUser.getAvatarUrl().lastIndexOf('/') + 1);
        }

        String newFilename = fileStorageService.storeAvatar(file, currentAvatarFilename);

        String avatarUrl = fromMethodName(UserController.class, "getAvatar", newFilename).build().toUriString();
        dbUser.setAvatarUrl(avatarUrl);
        userRepository.save(dbUser);

        return userMapper.toUserProfile(dbUser);
    }


    @Transactional
    public UserProfile deleteAvatar() {
        User dbUser = userUtil.getCurrentUserFromDb();

        dbUser.setAvatarUrl(null);
        userRepository.save(dbUser);

        return userMapper.toUserProfile(dbUser);
    }

    public Resource getAvatar(String filename) {
        return fileStorageService.loadAvatar(filename);
    }

    @Transactional(readOnly = true)
    public SavedAiSettings getAiSettings() {
        User dbUser = userUtil.getCurrentUserFromDb();


        if (dbUser.getAiSettings() == null) {
            throw new ResourceNotFoundException("AI-настройки не найдены");
        }

        return userMapper.toSavedAiSettings(dbUser.getAiSettings());
    }

    @Transactional
    public SavedAiSettings saveAiSettings(UpdateAiSettingsRequest request) {
        User dbUser = userUtil.getCurrentUserFromDb();
        AiSettings aiSettings = getOrCreateAiSettings(dbUser);

        userMapper.updateAiSettingsFromRequest(request, aiSettings);

        userRepository.save(dbUser);
        return userMapper.toSavedAiSettings(aiSettings);
    }

    @Transactional
    public void deleteAiSettings() {
        User dbUser = userUtil.getCurrentUserFromDb();

        if (dbUser.getAiSettings() != null) {
            dbUser.setAiSettings(null);
            userRepository.save(dbUser);
        }
    }



    private AiSettings getOrCreateAiSettings(User user) {
        AiSettings aiSettings = user.getAiSettings();
        if (aiSettings == null) {
            aiSettings = new AiSettings();
            aiSettings.setUser(user);
            user.setAiSettings(aiSettings);
        }
        return aiSettings;
    }
}
