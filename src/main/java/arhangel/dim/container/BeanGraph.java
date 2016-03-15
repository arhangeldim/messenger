package arhangel.dim.container;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
        BeanVertex beanV = new BeanVertex(value);
        vertices.put(beanV, new ArrayList<BeanVertex>());
        return beanV;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        List<BeanVertex> ways = vertices.get(from);
        ways.add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        List<BeanVertex> waysV1 = vertices.get(v1);
        List<BeanVertex> waysV2 = vertices.get(v2);
        return waysV1.contains(v2) || waysV2.contains(v1);
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

    private final void dfs(BeanVertex vertex, Stack<BeanVertex> result, Map<BeanVertex, Boolean> used) {
        try {
            used.put(vertex, false); //посетили эту вершину

            for (BeanVertex toVertex : vertices.get(vertex)) { //получаем вершины в которые есть путь
                if (!used.containsKey(toVertex)) {
                    dfs(toVertex, result, used);
                } else {
                    if (used.get(toVertex) == true) {
                        throw  new Exception("Found cycle in graph!");
                    }
                }
            }
            used.replace(vertex, false, true);
            result.push(vertex);
        } catch (Exception exc) {
            exc.printStackTrace(System.err);
        }
    }

    public List<BeanVertex> sort() {
        Stack<BeanVertex> result = new Stack<>();
        Map<BeanVertex, Boolean> used = new HashMap<>();
        for (BeanVertex vertex:vertices.keySet()) {
            if (!used.containsKey(vertex)) {
                dfs(vertex, result, used);
            }
        }
        return result;
    }
}
