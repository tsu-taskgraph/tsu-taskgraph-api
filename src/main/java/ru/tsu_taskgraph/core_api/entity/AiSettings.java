package ru.tsu_taskgraph.core_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ai_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AiProvider provider;

    private String model;

    private String encryptedApiKey;

    private String apiKeyMasked;

    private String ollamaBaseUrl;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_settings_id", referencedColumnName = "id")
    private ProviderSettings providerSettings;

    @OneToOne(mappedBy = "aiSettings")
    private User user;
}
