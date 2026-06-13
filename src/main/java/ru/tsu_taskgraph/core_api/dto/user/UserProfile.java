package ru.tsu_taskgraph.core_api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private UUID id;
    private String email;
    private String displayName;
    private String avatarUrl;
    private SavedAiSettings aiSettings;
    private LocalDateTime createdAt;
}
