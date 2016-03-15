package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex currentVertex = new BeanVertex(value);
        vertices.put(currentVertex, new ArrayList<>());
        return currentVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
        vertices.get(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        return vertices.get(v1).contains(v2);
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {
        return vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }


    private enum DfsColors {
        BLACK,
        GREY
    }

    private static Map<BeanVertex, DfsColors> color;
    private boolean cycleFound;

    private void dfs(BeanVertex vertex, List<BeanVertex> sorted) {
        color.put(vertex, DfsColors.GREY);
        for (BeanVertex v: vertices.get(vertex)) {
            if (cycleFound) {
                return;
            }
            if (color.containsKey(v)) {
                if (color.get(v) == DfsColors.GREY) {
                    cycleFound = true;
                    return;
                }
            } else {
                dfs(v, sorted);
            }
        }
        sorted.add(vertex);
        color.put(vertex, DfsColors.BLACK);
    }

    /**
     * Топологическая сортировка графа
     * @throws IllegalStateException если в графе есть цикл
     */
    public List<BeanVertex> getTopSort() throws InvalidConfigurationException {
        List<BeanVertex> result = new ArrayList<>();
        cycleFound = false;
        color = new HashMap<>();

        vertices.keySet().stream()
                .filter(v -> !color.containsKey(v))
                .forEach(v -> dfs(v, result));

        if (cycleFound) {
            throw new InvalidConfigurationException("Cycle Found");
        }
        return result;
    }

    /**
     * Проверяем, есть ли в графе цикл
     */
    public boolean containsCycle() {
        List<BeanVertex> result = new ArrayList<>();
        cycleFound = false;
        color = new HashMap<>();

        vertices.keySet().stream()
                .filter(v -> !color.containsKey(v))
                .forEach(v -> dfs(v, result));
        return cycleFound;
    }
}
