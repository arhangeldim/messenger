package arhangel.dim.container;

import sun.security.provider.certpath.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();
    private Stack<BeanVertex> stackBean = new Stack<>();
//    private Stack<BeanVertex> stackBean = new Stack<>();

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        this.vertices.put(vertex, new ArrayList<>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        this.vertices.get(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        return this.vertices.containsKey(v1) && this.vertices.get(v1).contains(v2);
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {
        return this.vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        if (this.vertices.isEmpty()) {
            return 0;
        }
        return this.vertices.size();
    }

    public List<BeanVertex> sort() {
        List<BeanVertex> sortedBeanVertexes = new ArrayList<>();
        for (BeanVertex v : this.vertices.keySet()) {
            this.sortStage(v);
        }
        while (!this.stackBean.isEmpty()) {
            sortedBeanVertexes.add(this.stackBean.pop());
        }
        return sortedBeanVertexes;
    }

    private void sortStage(BeanVertex vertex) {
        if (!this.vertices.get(vertex).isEmpty()) {
            for (BeanVertex v : this.vertices.get(vertex)) {
                this.sortStage(v);
            }
        }
        this.stackBean.push(vertex);
    }
}
