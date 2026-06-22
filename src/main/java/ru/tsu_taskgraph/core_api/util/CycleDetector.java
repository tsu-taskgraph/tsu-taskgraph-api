package ru.tsu_taskgraph.core_api.util;

import org.springframework.stereotype.Component;
import ru.tsu_taskgraph.core_api.dto.ai.SkeletonEdge;
import ru.tsu_taskgraph.core_api.dto.ai.SkeletonNode;
import ru.tsu_taskgraph.core_api.entity.Edge;
import ru.tsu_taskgraph.core_api.exception.CycleDetectedException;

import java.util.*;

/**
 * Утилита для обнаружения циклов в ориентированном графе задач.
 * Использует алгоритм Поиска в глубину (DFS).
 */
@Component
public class CycleDetector {

    /**
     * Проверяет, создаст ли новое ребро цикл. Бросает исключение при первом найденном цикле.
     * Оптимизирован для проверки одного добавляемого ребра.
     *
     * @param existingEdges Существующие рёбра в проекте.
     * @param newSourceId   ID исходной задачи (откуда идёт ребро).
     * @param newTargetId   ID целевой задачи (куда идёт ребро).
     * @throws CycleDetectedException если цикл обнаружен.
     */
    public void detectCycle(List<Edge> existingEdges, UUID newSourceId, UUID newTargetId) {
        Map<UUID, List<UUID>> adj = buildAdjacencyList(existingEdges, newSourceId, newTargetId);
        Set<UUID> visited = new HashSet<>();
        Set<UUID> recursionStack = new HashSet<>();
        if (isCyclicUtil(newTargetId, adj, visited, recursionStack, null)) {
            throw new CycleDetectedException("Добавление этой зависимости создаёт цикл в графе задач.");
        }
    }

    /**
     * Находит один любой цикл в графе, представленном в виде DTO (до сохранения в БД).
     *
     * @param nodes Список узлов-скелетонов.
     * @param edges Список рёбер-скелетонов.
     * @return Список строковых tempId узлов, образующих цикл (например, [A, B, C, A]), или пустой список, если циклов нет.
     */
    public List<String> findCycleInSkeleton(List<SkeletonNode> nodes, List<SkeletonEdge> edges) {
        Map<String, List<String>> adj = buildAdjacencyListFromSkeleton(nodes, edges);
        Set<String> allNodes = adj.keySet();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String node : allNodes) {
            if (!visited.contains(node)) {
                List<String> cyclePath = new ArrayList<>();
                if (isCyclicUtilString(node, adj, visited, recursionStack, cyclePath)) {
                    int cycleStartIndex = cyclePath.indexOf(cyclePath.get(cyclePath.size() - 1));
                    List<String> cycle = new ArrayList<>(cyclePath.subList(cycleStartIndex, cyclePath.size() - 1));
                    if (!cycle.isEmpty()) {
                        cycle.add(cycle.get(0));
                    }
                    return cycle;
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Находит один любой цикл в полном наборе рёбер-сущностей.
     *
     * @param edges Полный список рёбер для проверки.
     * @return Список UUID узлов, образующих цикл (например, [A, B, C, A]), или пустой список, если циклов нет.
     */
    public List<UUID> findCycleInEdges(List<Edge> edges) {
        Map<UUID, List<UUID>> adj = buildAdjacencyList(edges);
        Set<UUID> allNodes = adj.keySet();
        Set<UUID> visited = new HashSet<>();
        Set<UUID> recursionStack = new HashSet<>();

        for (UUID node : allNodes) {
            if (!visited.contains(node)) {
                List<UUID> cyclePath = new ArrayList<>();
                if (isCyclicUtil(node, adj, visited, recursionStack, cyclePath)) {
                    int cycleStartIndex = cyclePath.indexOf(cyclePath.get(cyclePath.size() - 1));
                    List<UUID> cycle = new ArrayList<>(cyclePath.subList(cycleStartIndex, cyclePath.size() - 1));
                    if (!cycle.isEmpty()) {
                        cycle.add(cycle.get(0));
                    }
                    return cycle;
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Строит список смежности на основе существующих рёбер и одного нового.
     */
    private Map<UUID, List<UUID>> buildAdjacencyList(List<Edge> edges, UUID newSourceId, UUID newTargetId) {
        Map<UUID, List<UUID>> adj = buildAdjacencyList(edges);
        adj.computeIfAbsent(newSourceId, k -> new ArrayList<>()).add(newTargetId);
        return adj;
    }

    /**
     * Строит список смежности из полного набора рёбер-сущностей.
     */
    private Map<UUID, List<UUID>> buildAdjacencyList(List<Edge> edges) {
        Map<UUID, List<UUID>> adj = new HashMap<>();
        for (Edge edge : edges) {
            UUID sourceId = edge.getSourceTask().getId();
            UUID targetId = edge.getTargetTask().getId();
            adj.computeIfAbsent(sourceId, k -> new ArrayList<>()).add(targetId);
            adj.computeIfAbsent(targetId, k -> new ArrayList<>());
        }
        return adj;
    }

    /**
     * Строит список смежности из DTO-скелетонов.
     */
    private Map<String, List<String>> buildAdjacencyListFromSkeleton(List<SkeletonNode> nodes, List<SkeletonEdge> edges) {
        Map<String, List<String>> adj = new HashMap<>();
        nodes.forEach(node -> adj.put(node.getTempId(), new ArrayList<>()));
        for (SkeletonEdge edge : edges) {
            adj.computeIfAbsent(edge.getSourceTempId(), k -> new ArrayList<>()).add(edge.getTargetTempId());
        }
        return adj;
    }

    /**
     * Рекурсивная утилита для Поиска в глубину (DFS) для графа с UUID.
     *
     * @param currentNode    Текущая вершина для проверки.
     * @param adj            Список смежности графа.
     * @param visited        Множество всех посещённых вершин.
     * @param recursionStack Множество вершин в текущем пути рекурсии ("серые" вершины).
     * @param currentPath    (Опционально) Список для отслеживания пути и восстановления цикла. Если null, путь не отслеживается.
     * @return true, если цикл обнаружен.
     */
    private boolean isCyclicUtil(UUID currentNode, Map<UUID, List<UUID>> adj, Set<UUID> visited, Set<UUID> recursionStack, List<UUID> currentPath) {
        visited.add(currentNode);
        recursionStack.add(currentNode);
        if (currentPath != null) {
            currentPath.add(currentNode);
        }

        List<UUID> neighbours = adj.get(currentNode);
        if (neighbours != null) {
            for (UUID neighbour : neighbours) {
                if (!visited.contains(neighbour)) {
                    if (isCyclicUtil(neighbour, adj, visited, recursionStack, currentPath)) {
                        return true;
                    }
                } else if (recursionStack.contains(neighbour)) {
                    if (currentPath != null) {
                        currentPath.add(neighbour);
                    }
                    return true;
                }
            }
        }

        recursionStack.remove(currentNode);
        if (currentPath != null) {
            currentPath.remove(currentPath.size() - 1);
        }
        return false;
    }

    /**
     * Рекурсивная утилита для Поиска в глубину (DFS) для графа со строковыми ID.
     */
    private boolean isCyclicUtilString(String currentNode, Map<String, List<String>> adj, Set<String> visited, Set<String> recursionStack, List<String> currentPath) {
        visited.add(currentNode);
        recursionStack.add(currentNode);
        currentPath.add(currentNode);

        List<String> neighbours = adj.get(currentNode);
        if (neighbours != null) {
            for (String neighbour : neighbours) {
                if (!visited.contains(neighbour)) {
                    if (isCyclicUtilString(neighbour, adj, visited, recursionStack, currentPath)) {
                        return true;
                    }
                } else if (recursionStack.contains(neighbour)) {
                    currentPath.add(neighbour);
                    return true;
                }
            }
        }

        recursionStack.remove(currentNode);
        currentPath.remove(currentPath.size() - 1);
        return false;
    }
}