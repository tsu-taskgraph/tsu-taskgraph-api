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
public class WikiPageSummaryDto {
    private UUID id;
    private UUID taskId;
    private String title;
    private AuthorType authorType;
    private Integer version;
    private LocalDateTime updatedAt;
}
