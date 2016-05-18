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
        BeanVertex newVert = new BeanVertex(value);
        vertices.put(newVert, new ArrayList<BeanVertex>());
        return newVert;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
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

    private enum Color {
        GRAY,
        BLACK
    }

    private void sortDfs(BeanVertex vertex,
                         Map<BeanVertex, Color> vertexColor,
                         List<BeanVertex> output) throws CycleReferenceException {
        vertexColor.put(vertex, Color.GRAY);
        for (BeanVertex neighbour: getLinked(vertex)) {
            if (vertexColor.containsKey(neighbour)) {
                if (vertexColor.get(neighbour) == Color.GRAY) {
                    throw new CycleReferenceException("There is cyclical references here. Sorry.");
                }
            } else {
                sortDfs(neighbour, vertexColor, output);
            }
        }

        vertexColor.put(vertex, Color.BLACK);
        output.add(vertex);
    }

    public List<BeanVertex> sort() throws CycleReferenceException {
        Map<BeanVertex, Color> vertexState = new HashMap<>();
        List<BeanVertex> order = new ArrayList<>();

        for (BeanVertex v: vertices.keySet()) {
            if (!vertexState.containsKey(v)) {
                sortDfs(v, vertexState, order);
            }
        }

        return order;
    }
}
