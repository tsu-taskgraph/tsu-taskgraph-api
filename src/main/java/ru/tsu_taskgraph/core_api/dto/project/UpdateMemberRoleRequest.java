package ru.tsu_taskgraph.core_api.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tsu_taskgraph.core_api.entity.ProjectRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRoleRequest {

    @NotNull
    private ProjectRole role;
}