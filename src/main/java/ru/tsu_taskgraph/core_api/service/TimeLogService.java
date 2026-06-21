package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tsu_taskgraph.core_api.domain.event.AuditEventPublisher;
import ru.tsu_taskgraph.core_api.dto.task.CreateTimeLogRequest;
import ru.tsu_taskgraph.core_api.dto.task.TimeLogDto;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TimeLog;
import ru.tsu_taskgraph.core_api.entity.User;
import ru.tsu_taskgraph.core_api.exception.BadRequestException;
import ru.tsu_taskgraph.core_api.mapper.TimeLogMapper;
import ru.tsu_taskgraph.core_api.repository.TimeLogRepository;
import ru.tsu_taskgraph.core_api.util.TaskUtil;
import ru.tsu_taskgraph.core_api.util.TimeLogUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeLogService {
    private final TimeLogRepository timeLogRepository;
    private final TimeLogMapper timeLogMapper;
    private final TaskUtil taskUtil;
    private final TimeLogUtil timeLogUtil;
    private final AuditEventPublisher auditEventPublisher;

    @Transactional
    public TimeLogDto createTimeLog(UUID taskId, CreateTimeLogRequest request, User currentUser) {
        Task task = taskUtil.getTaskById(taskId);

        TimeLog timeLog = TimeLog.builder()
                .task(task)
                .user(currentUser)
                .hours(request.getHours())
                .comment(request.getComment())
                .loggedAt(request.getLoggedAt() != null ? request.getLoggedAt() : LocalDateTime.now())
                .build();

        task.setLoggedHours(task.getLoggedHours() + request.getHours());

        timeLog = timeLogRepository.save(timeLog);

        auditEventPublisher.publishTimeLoggedEvent(this, timeLog, currentUser);

        return timeLogMapper.toDto(timeLog);
    }

    @Transactional(readOnly = true)
    public List<TimeLogDto> getTimeLogsByTask(UUID taskId) {
        Task task = taskUtil.getTaskById(taskId);
        List<TimeLog> timeLogs = timeLogRepository.findByTask(task);
        return timeLogMapper.toDtoList(timeLogs);
    }

    @Transactional
    public void deleteTimeLog(UUID taskId, UUID timeLogId) {
        TimeLog timeLog = timeLogUtil.getTimeLogById(timeLogId);

        if (!timeLog.getTask().getId().equals(taskId)) {
            throw new BadRequestException("Трудозатраты с id=" + timeLogId + " не относятся к Задаче с id=" + taskId);
        }

        Task task = timeLog.getTask();
        task.setLoggedHours(task.getLoggedHours() - timeLog.getHours());

        timeLogRepository.deleteById(timeLogId);
    }
}
