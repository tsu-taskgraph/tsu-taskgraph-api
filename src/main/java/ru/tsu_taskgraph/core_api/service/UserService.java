package ru.tsu_taskgraph.core_api.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.tsu_taskgraph.core_api.dto.user.SavedAiSettings;
import ru.tsu_taskgraph.core_api.dto.user.UpdateAiSettingsRequest;
import ru.tsu_taskgraph.core_api.dto.user.UpdateProfileRequest;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;

@Service
public class UserService {

    public UserProfile getCurrentUserProfile() {
        return null;
    }

    public UserProfile updateCurrentUser(UpdateProfileRequest request) {
        return null;
    }

    public UserProfile uploadAvatar(MultipartFile file) {
        return null;
    }

    public UserProfile deleteAvatar() {
        return null;
    }

    public Resource getAvatar(String filename) {
        return null;
    }

    public SavedAiSettings getAiSettings() {
        return null;
    }

    public SavedAiSettings saveAiSettings(UpdateAiSettingsRequest request) {
        return null;
    }

    public void deleteAiSettings() {
    }
}
