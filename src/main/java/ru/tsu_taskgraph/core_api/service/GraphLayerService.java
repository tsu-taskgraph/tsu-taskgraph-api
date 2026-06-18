package ru.tsu_taskgraph.core_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.entity.Task;
import ru.tsu_taskgraph.core_api.repository.EdgeRepository;
import ru.tsu_taskgraph.core_api.repository.TaskRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphLayerService {

    private final TaskRepository taskRepository;
    private final EdgeRepository edgeRepository;

    /**
     * Рассчитывает слой для каждой задачи в проекте, используя алгоритм Кана (топологическая сортировка).
     *
     * @param projectId ID проекта.
     * @return Map, где ключ - ID задачи, а значение - номер ее слоя.
     */
    public Map<UUID, Integer> calculateLayers(UUID projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        List<Edge> edges = edgeRepository.findByProjectId(projectId);

        Map<UUID, Integer> inDegree = new HashMap<>();
        Map<UUID, List<UUID>> adj = new HashMap<>();

        for (Task task : tasks) {
            inDegree.put(task.getId(), 0);
            adj.put(task.getId(), new ArrayList<>());
        }

        for (Edge edge : edges) {
            UUID source = edge.getSourceTask().getId();
            UUID target = edge.getTargetTask().getId();
            adj.get(source).add(target);
            inDegree.put(target, inDegree.get(target) + 1);
        }

        Queue<UUID> queue = new LinkedList<>();
        for (Map.Entry<UUID, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        Map<UUID, Integer> layers = new HashMap<>();
        int currentLayer = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                UUID u = queue.poll();
                layers.put(u, currentLayer);

                for (UUID v : adj.get(u)) {
                    inDegree.put(v, inDegree.get(v) - 1);
                    if (inDegree.get(v) == 0) {
                        queue.add(v);
                    }
                }
            }
            currentLayer++;
        }

        // На случай, если в графе есть циклы (хотя их быть не должно),
        // присваиваем оставшимся задачам максимальный слой.
        for (Task task : tasks) {
            layers.putIfAbsent(task.getId(), currentLayer);
        }

        return layers;
    }
}
