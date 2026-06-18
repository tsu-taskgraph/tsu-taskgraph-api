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

    public abstract ProjectDto toDto(Project project);

    public abstract ProjectMemberDto toMemberDto(ProjectMember member);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateProjectFromDto(UpdateProjectRequest dto, @MappingTarget Project project);

    @AfterMapping
    protected void calculateMetrics(Project project, @MappingTarget ProjectDto dto) {
        ProjectMetricsDto metrics = calculationService.calculateProjectMetrics(project.getId());
        dto.setTotalEstimatedHours(metrics.getTotalEstimatedHours());
        dto.setTotalLoggedHours(metrics.getTotalLoggedHours());
        dto.setCompletionPercent(metrics.getCompletionPercent());
    }
}
