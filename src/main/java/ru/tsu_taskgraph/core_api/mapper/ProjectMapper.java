package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.*;
import ru.tsu_taskgraph.core_api.dto.project.ProjectDto;
import ru.tsu_taskgraph.core_api.dto.project.ProjectMemberDto;
import ru.tsu_taskgraph.core_api.dto.project.UpdateProjectRequest;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ProjectDto toDto(Project project);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "user.id", target = "userId")
    ProjectMemberDto toMemberDto(ProjectMember member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "teamSize", ignore = true)
    @Mapping(target = "totalEstimatedHours", ignore = true)
    @Mapping(target = "totalLoggedHours", ignore = true)
    @Mapping(target = "completionPercent", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDto(UpdateProjectRequest request, @MappingTarget Project project);
}
