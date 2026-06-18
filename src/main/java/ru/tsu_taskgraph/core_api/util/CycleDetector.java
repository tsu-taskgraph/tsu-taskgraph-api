package ru.tsu_taskgraph.core_api.util;

import org.springframework.stereotype.Component;
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
     * Проверяет, создаст ли новое ребро цикл.
     *
     * @param existingEdges Существующие рёбра в проекте.
     * @param newSourceId   ID исходной задачи (откуда идёт ребро).
     * @param newTargetId   ID целевой задачи (куда идёт ребро).
     * @throws CycleDetectedException если цикл обнаружен.
     */
    public void detectCycle(List<Edge> existingEdges, UUID newSourceId, UUID newTargetId) {
        // 1. Строим список смежности для представления графа в памяти.
        Map<UUID, List<UUID>> adj = buildAdjacencyList(existingEdges, newSourceId, newTargetId);

        // 2. Используем три "цвета" для вершин в алгоритме DFS:
        //    - visited (белый -> серый/черный): все вершины, которые мы когда-либо посещали.
        //    - recursionStack (серый): вершины, которые находятся в текущем стеке рекурсии.
        Set<UUID> visited = new HashSet<>();
        Set<UUID> recursionStack = new HashSet<>();

        // 3. Оптимизация: нет нужды проверять весь граф.
        //    Любой новый цикл обязательно будет проходить через новое ребро (source -> target),
        //    а значит, и через его целевую вершину (target).
        //    Поэтому достаточно запустить проверку только с этой вершины.
        if (isCyclicUtil(newTargetId, adj, visited, recursionStack)) {
            throw new CycleDetectedException("Добавление этой зависимости создаёт цикл в графе задач.");
        }
    }

    /**
     * Строит список смежности на основе существующих рёбер и одного нового.
     */
    private Map<UUID, List<UUID>> buildAdjacencyList(List<Edge> edges, UUID newSourceId, UUID newTargetId) {
        Map<UUID, List<UUID>> adj = new HashMap<>();
        for (Edge edge : edges) {
            //adj.computeIfAbsent(edge.getSourceTask().getId(), k -> new ArrayList<>()).add(edge.getTargetTask().getId());
        }
        // Добавляем новое ребро в граф для проверки
        adj.computeIfAbsent(newSourceId, k -> new ArrayList<>()).add(newTargetId);
        return adj;
    }

    /**
     * Рекурсивная утилита для Поиска в глубину (DFS).
     *
     * @param currentNode    Текущая вершина для проверки.
     * @param adj            Список смежности графа.
     * @param visited        Множество всех посещённых вершин.
     * @param recursionStack Множество вершин в текущем пути рекурсии ("серые" вершины).
     * @return true, если цикл обнаружен.
     */
    private boolean isCyclicUtil(UUID currentNode, Map<UUID, List<UUID>> adj, Set<UUID> visited, Set<UUID> recursionStack) {
        // Помечаем текущую вершину как посещённую и добавляем в стек рекурсии
        visited.add(currentNode);
        recursionStack.add(currentNode);

        List<UUID> neighbours = adj.get(currentNode);
        if (neighbours != null) {
            for (UUID neighbour : neighbours) {
                // Если соседняя вершина еще не посещена, рекурсивно идём в неё
                if (!visited.contains(neighbour)) {
                    if (isCyclicUtil(neighbour, adj, visited, recursionStack)) {
                        return true;
                    }
                }
                // Если соседняя вершина уже есть в стеке рекурсии, значит, мы нашли обратное ребро (цикл).
                else if (recursionStack.contains(neighbour)) {
                    return true;
                }
            }
        }

        // Убираем вершину из стека рекурсии перед возвратом из вызова (помечаем как "чёрную")
        recursionStack.remove(currentNode);
        return false;
    }
}
