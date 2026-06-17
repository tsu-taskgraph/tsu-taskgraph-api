package ru.tsu_taskgraph.core_api.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {
    @NotBlank(message = "Имя проекта не может быть пустым")
    private String name;

    private String description;

    private List<String> techStack;

    private Boolean aiEstimate;
}
