package ru.tsu_taskgraph.core_api.util;

import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.TaskStatus;

import java.util.Map;
import java.util.Optional;

import static ru.tsu_taskgraph.core_api.entity.TaskStatus.*;

@Component
public class TaskStatusTransitionUtil {

    private static final Map<TaskStatus, Map<TaskStatus, TaskStatus>> MATRIX = Map.of(
            LOCKED, Map.of(
                    LOCKED, LOCKED,
                    AVAILABLE, LOCKED,
                    IN_PROGRESS, LOCKED,
                    SKIPPED, SKIPPED
            ),
            AVAILABLE, Map.of(
                    LOCKED, LOCKED,
                    AVAILABLE, LOCKED,
                    IN_PROGRESS, LOCKED,
                    SKIPPED, SKIPPED
            ),
            IN_PROGRESS, Map.of(
                    LOCKED, LOCKED,
                    AVAILABLE, LOCKED,
                    IN_PROGRESS, LOCKED,
                    SKIPPED, SKIPPED
            ),
            COMPLETED, Map.of(
                    LOCKED, AVAILABLE, // Маркер для tryToUnlock
                    AVAILABLE, AVAILABLE,
                    IN_PROGRESS, IN_PROGRESS,
                    SKIPPED, SKIPPED,
                    COMPLETED, COMPLETED
            ),
            SKIPPED, Map.of(
                    LOCKED, AVAILABLE, // Маркер для tryToUnlock
                    AVAILABLE, AVAILABLE,
                    IN_PROGRESS, IN_PROGRESS,
                    SKIPPED, SKIPPED,
                    COMPLETED, COMPLETED
            )
    );

    public Optional<TaskStatus> getNewTargetStatus(TaskStatus sourceStatus, TaskStatus targetStatus) {
        return Optional.ofNullable(MATRIX.get(sourceStatus).get(targetStatus));
    }
}
