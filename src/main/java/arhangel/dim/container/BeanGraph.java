package arhangel.dim.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
        BeanVertex vertex = new BeanVertex(value);
        vertices.put(vertex, new ArrayList<BeanVertex>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        getLinked(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        return getLinked(v1).contains(v2);
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

    private enum VisitState {
        VISITING,
        VISITED
    }

    private void sortDfs(BeanVertex vertex, Map<BeanVertex, VisitState> vertexState, List<BeanVertex> output)
            throws CycleReferenceException {
        vertexState.put(vertex, VisitState.VISITING);
        for (BeanVertex neighbour: getLinked(vertex)) {
            if (vertexState.containsKey(neighbour)) {
                if (vertexState.get(neighbour) == VisitState.VISITING) {
                    throw new CycleReferenceException("Bean graph contains cyclical references.");
                }
            } else {
                sortDfs(neighbour, vertexState, output);
            }
        }

        vertexState.put(vertex, VisitState.VISITED);
        output.add(vertex);
    }

    /**
     * Отсортировать граф топологически.
     */
    public List<BeanVertex> sortBeans() throws CycleReferenceException {
        Map<BeanVertex, VisitState> vertexState = new HashMap<>();
        List<BeanVertex> order = new ArrayList<>();

        for (BeanVertex v: vertices.keySet()) {
            if (!vertexState.containsKey(v)) {
                sortDfs(v, vertexState, order);
            }
        }

        return order;
    }
}
