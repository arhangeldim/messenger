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
    private List<BeanVertex> topSorted;

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        vertices.put(vertex, new ArrayList<>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) throws InvalidConfigurationException {
        if (from == null || to == null || !vertices.containsKey(from)) {
            throw new InvalidConfigurationException("Attempt to add invalid edge");
        }
        vertices.get(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex from, BeanVertex to) throws InvalidConfigurationException {
        if (from == null || to == null || !vertices.containsKey(from)) {
            throw new InvalidConfigurationException("Attempt to check " +
                    " connection with nonexistent vertex");
        }
        return getLinked(from).contains(to);
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

    private void dfs(BeanVertex vertex) throws CycleReferenceException {
        vertex.color = BeanVertex.ColorType.GREY;
        for (BeanVertex to : getLinked(vertex)) {
            if (to.color == BeanVertex.ColorType.GREY) {
                throw new CycleReferenceException();
            }
            if (to.color == BeanVertex.ColorType.BLACK) {
                dfs(to);
            }
        }
        vertex.color = BeanVertex.ColorType.WHITE;
        topSorted.add(vertex);
    }

    public List<BeanVertex> topSort() throws CycleReferenceException {
        topSorted = new ArrayList<>();
        for (BeanVertex vertex : vertices.keySet()) {
            if (vertex.color == BeanVertex.ColorType.BLACK) {
                dfs(vertex);
            }
        }
        return topSorted;
    }
}
