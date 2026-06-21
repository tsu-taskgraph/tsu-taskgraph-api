package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tsu_taskgraph.core_api.dto.task.AssigneeDto;
import ru.tsu_taskgraph.core_api.dto.user.SavedAiSettings;
import ru.tsu_taskgraph.core_api.dto.user.UpdateAiSettingsRequest;
import ru.tsu_taskgraph.core_api.dto.user.UserProfile;
import ru.tsu_taskgraph.core_api.entity.AiProviderSettings;
import ru.tsu_taskgraph.core_api.entity.AiSettings;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.service.EncryptionService;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private EncryptionService encryptionService;

    public abstract UserProfile toUserProfile(User user);

    @Mapping(source = "id", target = "userId")
    public abstract AssigneeDto toAssigneeDto(User user);

    @Mapping(target = "hasApiKey", expression = "java(aiSettings.getEncryptedApiKey() != null && !aiSettings.getEncryptedApiKey().isEmpty())")
    public abstract SavedAiSettings toSavedAiSettings(AiSettings aiSettings);

    public abstract ru.tsu_taskgraph.core_api.dto.user.AiProviderSettings toProviderSettingsDto(AiProviderSettings aiProviderSettings);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aiSettings", ignore = true)
    public abstract void updateProviderSettingsFromDto(ru.tsu_taskgraph.core_api.dto.user.AiProviderSettings dto, @MappingTarget AiProviderSettings entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "encryptedApiKey", ignore = true)
    @Mapping(target = "apiKeyMasked", ignore = true)
    @Mapping(target = "aiProviderSettings", ignore = true)
    public abstract void updateAiSettingsFromRequest(UpdateAiSettingsRequest request, @MappingTarget AiSettings aiSettings);

    @AfterMapping
    protected void afterUpdateAiSettingsFromRequest(UpdateAiSettingsRequest request, @MappingTarget AiSettings aiSettings) {
        updateApiKey(aiSettings, request.getApiKey());
        updateAiProviderSettings(aiSettings, request.getAiProviderSettings());
    }

    private void updateApiKey(AiSettings aiSettings, String apiKey) {
        if (apiKey != null) {
            aiSettings.setEncryptedApiKey(encryptionService.encrypt(apiKey));
            aiSettings.setApiKeyMasked(maskApiKey(apiKey));
        } else {
            aiSettings.setEncryptedApiKey(null);
            aiSettings.setApiKeyMasked(null);
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey.length() > 7) {
            return apiKey.substring(0, 3) + "..." + apiKey.substring(apiKey.length() - 4);
        } else if (apiKey.length() > 3) {
            return apiKey.substring(0, 3) + "...";
        } else {
            return apiKey + "...";
        }
    }

    private void updateAiProviderSettings(AiSettings aiSettings, ru.tsu_taskgraph.core_api.dto.user.AiProviderSettings aiProviderSettingsDto) {
        if (aiProviderSettingsDto != null) {
            AiProviderSettings aiProviderSettings = aiSettings.getAiProviderSettings();
            if (aiProviderSettings == null) {
                aiProviderSettings = new AiProviderSettings();
                aiProviderSettings.setAiSettings(aiSettings);
                aiSettings.setAiProviderSettings(aiProviderSettings);
            }
            updateProviderSettingsFromDto(aiProviderSettingsDto, aiProviderSettings);
        } else {
            aiSettings.setAiProviderSettings(null);
        }
    }
}