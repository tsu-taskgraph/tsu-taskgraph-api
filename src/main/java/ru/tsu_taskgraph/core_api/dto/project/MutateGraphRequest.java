package ru.tsu_taskgraph.core_api.dto.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MutateGraphRequest {
    @NotBlank
    private String prompt;
}