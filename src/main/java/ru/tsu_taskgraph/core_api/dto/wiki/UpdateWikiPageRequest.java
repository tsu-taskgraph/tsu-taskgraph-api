package ru.tsu_taskgraph.core_api.dto.wiki;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWikiPageRequest {
    private String title;

    @NotBlank
    private String content;
}
