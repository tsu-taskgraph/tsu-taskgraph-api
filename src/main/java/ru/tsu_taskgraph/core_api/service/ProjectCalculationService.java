package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tsu_taskgraph.core_api.dto.project.ProjectMetricsDto;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCalculationService {

    private final TaskRepository taskRepository;

    public ProjectMetricsDto calculateProjectMetrics(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        if (tasks.isEmpty()) {
            return new ProjectMetricsDto(0.0, 0.0, 0.0);
        }

        double totalEstimated = tasks.stream()
                .mapToDouble(task -> task.getEstimatedHours() != null ? task.getEstimatedHours() : 0)
                .sum();

        double totalLogged = tasks.stream()
                .mapToDouble(Task::getLoggedHours)
                .sum();

        long completedTasksCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.SKIPPED)
                .count();

        double completionPercent = ((double) completedTasksCount / tasks.size()) * 100;

        return new ProjectMetricsDto(totalEstimated, totalLogged, round(completionPercent, 2));
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
