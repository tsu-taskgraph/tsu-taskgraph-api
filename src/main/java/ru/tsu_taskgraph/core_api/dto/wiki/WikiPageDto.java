package ru.tsu_taskgraph.core_api.dto.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.AuthorType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiPageDto {
    private UUID id;
    private UUID projectId;
    private UUID taskId;
    private String title;
    private String content;
    private UUID authorId;
    private AuthorType authorType;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
