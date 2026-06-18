package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.project.EdgeResponse;
import ru.tsu_taskgraph.core_api.dto.project.ProjectGraphResponse;
import ru.tsu_taskgraph.core_api.dto.task.TaskNode;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.mapper.EdgeMapper;
import ru.tsu_taskgraph.core_api.mapper.TaskMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectGraphService {

    private final ProjectUtil projectUtil;
    private final TaskRepository taskRepository;
    private final EdgeRepository edgeRepository;
    private final TaskMapper taskMapper;
    private final EdgeMapper edgeMapper;
    private final GraphLayerService graphLayerService;

    @Transactional(readOnly = true)
    public ProjectGraphResponse getProjectGraph(UUID projectId) {
        Project project = projectUtil.getProjectById(projectId);

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        Map<UUID, Integer> layers = graphLayerService.calculateLayers(projectId);
        List<TaskNode> taskNodes = taskMapper.toNodeList(tasks, layers);

        List<Edge> edges = edgeRepository.findByProjectId(projectId);
        List<EdgeResponse> edgeResponses = edgeMapper.toDtoList(edges);

        return ProjectGraphResponse.builder()
                .projectId(projectId)
                .nodes(taskNodes)
                .edges(edgeResponses)
                .enrichmentStatus(project.getEnrichmentStatus())
                .build();
    }
}
