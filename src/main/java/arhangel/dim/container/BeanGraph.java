package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();
    private Map<String, BeanVertex> names = new HashMap<>();
    private List<BeanVertex> sorted = new ArrayList<>();
    private Map<BeanVertex, Integer> used = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        vertices.put(vertex, new ArrayList<>());
        names.put(value.getName(), vertex);
        for (Property p: vertex.getBean().getProperties().values()) {
            if (p.getType() == ValueType.REF) {
                addEdge(vertex, names.get(p.getValue()));
            }
        }
        return vertex;
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

    public List<BeanVertex> sort() {
        used.clear();
        for (BeanVertex vertex: vertices.keySet()) {
            used.put(vertex, 0);
        }
        for (BeanVertex vertex: vertices.keySet()) {
            if (used.get(vertex) == 0 ) {
                dfs(vertex);
            }
        }
        Collections.reverse(sorted);
        return sorted;
    }

    private void dfs(BeanVertex vertex) {
        used.put(vertex, 1);
        for (BeanVertex u: getLinked(vertex)) {
            if (used.get(u) == 0) {
                dfs(u);
            }
        }
        sorted.add(vertex);
    }

    public List<Bean> getSortedBeans() {
        List<Bean> result = new ArrayList<>();
        List<BeanVertex> sorted = this.sort();
        for (BeanVertex vertex: sorted) {
            result.add(vertex.getBean());
        }
        return result;
    }

}