package ru.tsu_taskgraph.core_api.dto.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiMediaDto {
    private UUID id;
    private UUID projectId;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private Long sizeBytes;
    private String url;
    private UUID uploadedBy;
    private LocalDateTime createdAt;
}
