package arhangel.dim.container.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Graph<V> {
    private Map<Vertex<V>, List<Vertex<V>>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public Vertex<V> addVertex(V value) {
        Vertex<V> vertex = new Vertex<>(value);
        vertices.put(vertex, new ArrayList<>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     * @param isDirected - если true, то связь односторонняя, иначе - двухсторонняя
     */
    public void addEdge(Vertex<V> from, Vertex<V> to, boolean isDirected) {
        if (vertices.get(from) != null) {
            vertices.get(from).add(to);
            if (!isDirected && vertices.get(to) != null) {
                vertices.get(to).add(from);
            }
        }
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(Vertex<V> v1, Vertex<V> v2) {
        return getLinked(v1).contains(v2);
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<Vertex<V>> getLinked(Vertex<V> vertex) {
        return vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    private LinkedList<Vertex<V>> sorted = new LinkedList<>();

    public void dfs() {
        vertices.keySet().stream().filter(vertex -> vertex.getState() != Vertex.State.VISITED).forEach(this::dfs);
    }

    public void dfs(Vertex<V> current) {

        current.setState(Vertex.State.MARKED);
        for (Vertex<V> vertex : getLinked(current)) {
            if (vertex.getState() == Vertex.State.MARKED) {
                System.out.println("Cycle: " + current + "->" + vertex);
                return;
            }
            if (vertex.getState() != Vertex.State.VISITED) {
                dfs(vertex);
            }
        }
        current.setState(Vertex.State.VISITED);
        sorted.push(current);
    }

    public List<Vertex<V>> toposort() {
        dfs();
        System.out.println(sorted);
        return sorted;
    }

}
