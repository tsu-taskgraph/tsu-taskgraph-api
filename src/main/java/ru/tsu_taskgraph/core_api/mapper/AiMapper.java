package ru.tsu_taskgraph.core_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonRequest;
import ru.tsu_taskgraph.core_api.dto.ai.ProviderConfig;
import ru.tsu_taskgraph.core_api.entity.Project;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiMapper {
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "techStack", source = "project.techStack")
    @Mapping(target = "description", source = "project.description")
    @Mapping(target = "teamSize", source = "project.teamSize")
    @Mapping(target = "aiEstimate", source = "project.aiEstimate")
    @Mapping(target = "providerConfig", source = "providerConfig")
    GenerateSkeletonRequest toGenerateSkeletonRequest(Project project, ProviderConfig providerConfig);
}