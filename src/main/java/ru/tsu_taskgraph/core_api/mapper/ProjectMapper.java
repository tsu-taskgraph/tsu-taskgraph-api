package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tsu_taskgraph.core_api.dto.project.ProjectDto;
import ru.tsu_taskgraph.core_api.dto.project.ProjectMemberDto;
import ru.tsu_taskgraph.core_api.dto.project.ProjectMetricsDto;
import ru.tsu_taskgraph.core_api.dto.project.UpdateProjectRequest;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.ProjectMember;
import ru.tsu_taskgraph.core_api.service.ProjectCalculationService;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ProjectMapper {

    @Autowired
    private ProjectCalculationService calculationService;

    @Mapping(source = "owner.id", target = "ownerId")
    public abstract ProjectDto toDto(Project project);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "user.id", target = "userId")
    public abstract ProjectMemberDto toMemberDto(ProjectMember member);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enrichmentStatus", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "teamSize", ignore = true)
    @Mapping(target = "totalEstimatedHours", ignore = true)
    @Mapping(target = "totalLoggedHours", ignore = true)
    @Mapping(target = "completionPercent", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void updateProjectFromDto(UpdateProjectRequest dto, @MappingTarget Project project);

    @AfterMapping
    protected void calculateMetrics(Project project, @MappingTarget ProjectDto dto) {
        ProjectMetricsDto metrics = calculationService.calculateProjectMetrics(project.getId());
        dto.setTotalEstimatedHours(metrics.getTotalEstimatedHours());
        dto.setTotalLoggedHours(metrics.getTotalLoggedHours());
        dto.setCompletionPercent(metrics.getCompletionPercent());
    }
}
