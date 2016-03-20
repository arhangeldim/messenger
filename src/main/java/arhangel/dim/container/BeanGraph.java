package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        if (!vertices.containsKey(vertex)) {
            List<BeanVertex> vertexList = new ArrayList<>();
            vertices.put(vertex, vertexList);
            return vertex;
        }
        return null;
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        if (!vertices.get(from).contains(to)) {
            vertices.get(from).add(to);
        }
    }

    public void removeEdge(BeanVertex from, BeanVertex to) {
        if (vertices.get(from).contains(to)) {
            vertices.get(from).remove(to);
        }
    }
    /**
     * Проверяем, связаны ли вершины
     */
    
    public boolean isConnected(BeanVertex from, BeanVertex to) {
        return vertices.get(from).contains(to);
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
}
