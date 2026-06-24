package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.project.CreateEdgeRequest;
import ru.tsu_taskgraph.core_api.dto.project.EdgeResponse;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.mapper.EdgeMapper;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.util.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EdgeService {

    private final EdgeRepository edgeRepository;
    private final EdgeMapper edgeMapper;
    private final TaskUtil taskUtil;
    private final EdgeUtil edgeUtil;
    private final CycleDetector cycleDetector;
    private final TaskStatusService taskStatusService;
    private final TaskStatusTransitionUtil taskStatusTransitionUtil;
    private final AuditEventPublisher auditEventPublisher;
    private final UserUtil userUtil;

    @Transactional
    public EdgeResponse createEdge(UUID projectId, CreateEdgeRequest request) {
        Task sourceTask = taskUtil.getTaskById(request.getSourceTaskId());
        Task targetTask = taskUtil.getTaskById(request.getTargetTaskId());
        User currentUser = userUtil.getCurrentUserFromContext();

        validateEdgeCreation(projectId, sourceTask, targetTask);

        Edge edge = Edge.builder()
                .project(sourceTask.getProject())
                .sourceTask(sourceTask)
                .targetTask(targetTask)
                .build();
        edge = edgeRepository.save(edge);

        applyStatusChangeMatrix(sourceTask, targetTask);

        auditEventPublisher.publishEdgeCreatedEvent(this, edge, currentUser);

        return edgeMapper.toDto(edge);
    }

    private void applyStatusChangeMatrix(Task sourceTask, Task targetTask) {
        Optional<TaskStatus> optionalNewStatus = taskStatusTransitionUtil
                .getNewTargetStatus(sourceTask.getStatus(), targetTask.getStatus());

        TaskStatus newTargetStatus = optionalNewStatus
                .orElseThrow(() -> new BadRequestException("Нельзя создавать зависимость к уже завершенной задаче."));

        if (newTargetStatus == TaskStatus.AVAILABLE && targetTask.getStatus() == TaskStatus.LOCKED) {
            taskStatusService.tryToUnlockTask(targetTask);
        } else {
            targetTask.setStatus(newTargetStatus);
        }
    }

    @Transactional
    public void deleteEdge(UUID edgeId) {
        Edge edge = edgeUtil.getEdgeById(edgeId);
        User currentUser = userUtil.getCurrentUserFromContext();
        Task targetTask = edge.getTargetTask();
        Task sourceTask = edge.getSourceTask();

        edgeRepository.delete(edge);

        auditEventPublisher.publishEdgeDeletedEvent(this, edge, currentUser);

        taskStatusService.tryToUnlockTask(sourceTask);
        taskStatusService.tryToUnlockTask(targetTask);
    }

    private void validateEdgeCreation(UUID projectId, Task sourceTask, Task targetTask) {
        if (!sourceTask.getProject().getId().equals(projectId) || !targetTask.getProject().getId().equals(projectId)) {
            throw new BadRequestException("Обе задачи должны принадлежать указанному проекту.");
        }
        if (sourceTask.getId().equals(targetTask.getId())) {
            throw new BadRequestException("Нельзя установить зависимость задачи на саму себя.");
        }
        if (edgeRepository.existsBySourceTaskAndTargetTask(sourceTask, targetTask)) {
            throw new BadRequestException("Такая зависимость уже существует.");
        }
        List<Edge> existingEdges = edgeRepository.findByProjectId(projectId);
        cycleDetector.detectCycle(existingEdges, sourceTask.getId(), targetTask.getId());
    }
}