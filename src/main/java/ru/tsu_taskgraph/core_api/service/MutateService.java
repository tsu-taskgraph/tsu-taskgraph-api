package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.dto.ai.*;
import ru.tsu_taskgraph.core_api.dto.project.MutateGraphRequest;
import ru.tsu_taskgraph.core_api.dto.project.ProjectGraphResponse;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Project;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.exception.AiCycleException;
import ru.tsu_taskgraph.core_api.mapper.AiMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;
import ru.tsu_taskgraph.core_api.util.CycleDetector;
import ru.tsu_taskgraph.core_api.util.ProjectUtil;
import ru.tsu_taskgraph.core_api.util.TaskUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MutateService {

    private final ProjectUtil projectUtil;
    private final AiService aiService;
    private final TaskRepository taskRepository;
    private final EdgeRepository edgeRepository;
    private final CycleDetector cycleDetector;
    private final ProjectGraphService projectGraphService;
    private final TaskUtil taskUtil;
    private final SmartRecoveryService smartRecoveryService;
    private final AiMapper aiMapper;

    @Transactional
    public ProjectGraphResponse mutateGraph(UUID projectId, MutateGraphRequest request, AiRequestConfig aiConfig, boolean enableSmartRecovery) {
        Project project = projectUtil.getProjectById(projectId);
        MutationPatch patch = aiService.getMutationPatch(project, request.getPrompt(), aiConfig);

        applyPatch(project, patch, enableSmartRecovery, aiConfig);

        // TODO: Опубликовать событие GRAPH_MUTATED

        return projectGraphService.getProjectGraph(projectId);
    }

    private void applyPatch(Project project, MutationPatch patch, boolean enableSmartRecovery, AiRequestConfig aiConfig) {
        Map<String, Task> tempIdToTaskMap = new HashMap<>();
        List<Edge> allEdges = new ArrayList<>(edgeRepository.findByProjectId(project.getId()));

        // 1. Создаем новые задачи
        if (patch.getNewNodes() != null) {
            patch.getNewNodes().forEach(nodeDto -> {
                Task newTask = Task.builder()
                        .project(project)
                        .title(nodeDto.getTitle())
                        .description(nodeDto.getDescription())
                        .category(nodeDto.getCategory())
                        .estimatedHours(nodeDto.getEstimatedHours())
                        .build();
                Task savedTask = taskRepository.save(newTask);
                tempIdToTaskMap.put(nodeDto.getTempId(), savedTask);
            });
        }

        // 2. Создаем новые ребра
        if (patch.getNewEdges() != null) {
            patch.getNewEdges().forEach(edgeDto -> {
                Task source = resolveTask(edgeDto.getSourceTempId(), tempIdToTaskMap);
                Task target = resolveTask(edgeDto.getTargetTempId(), tempIdToTaskMap);

                Edge newEdge = Edge.builder()
                        .project(project)
                        .sourceTask(source)
                        .targetTask(target)
                        .build();
                allEdges.add(edgeRepository.save(newEdge));
            });
        }

        // 3. Проверяем на циклы
        List<UUID> cycle = cycleDetector.findCycleInEdges(allEdges);
        if (!cycle.isEmpty()) {
            if (enableSmartRecovery) {
                log.warn("Обнаружен цикл в мутации графа для проекта {}. Запуск Smart Recovery.", project.getId());
                List<Task> tasks = taskRepository.findByProjectId(project.getId());
                GraphSnapshot snapshot = aiMapper.createGraphSnapshot(tasks, allEdges);
                SmartRecoveryRequest recoveryRequest = SmartRecoveryRequest.builder()
                        .currentGraph(snapshot)
                        .failedMutation(patch)
                        .cycleNodes(getCycleIds(cycle))
                        .projectName(project.getName())
                        .techStack(project.getTechStack())
                        .aiEstimate(project.getAiEstimate())
                        .providerConfig(aiService.resolveProviderConfig(aiConfig, project.getOwner()))
                        .build();
                SmartRecoveryResponse recoveryResponse = smartRecoveryService.recover(recoveryRequest);
                // Рекурсивно применяем исправленный патч
                applyPatch(project, recoveryResponse.getFixedPatch(), false, aiConfig);
            } else {
                throw new AiCycleException("Обнаружен цикл в графе", getCycleIds(cycle), true);
            }
        }
    }

    private Task resolveTask(String id, Map<String, Task> tempIdMap) {
        if (tempIdMap.containsKey(id)) {
            return tempIdMap.get(id);
        }
        return taskUtil.getTaskById(UUID.fromString(id));
    }

    private List<String> getCycleIds(List<UUID> cycle) {
        return cycle.stream().map(UUID::toString).collect(Collectors.toList());
    }
}