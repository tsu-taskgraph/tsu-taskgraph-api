package ru.tsu_taskgraph.core_api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.entity.TimeLog;
import ru.tsu_taskgraph.core_api.exception.ResourceNotFoundException;
import ru.tsu_taskgraph.core_api.repository.TimeLogRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TimeLogUtil {

    private final TimeLogRepository timeLogRepository;

    public TimeLog getTimeLogById(UUID timeLogId) {
        return timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new ResourceNotFoundException("Трудозатраты с id=" + timeLogId + " не найдены"));
    }
}
