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

    private final void dfs(BeanVertex vertex, Stack<BeanVertex> result, ArrayList<BeanVertex> used) {
        used.add(vertex); //посетили эту вершину
        for (BeanVertex toVertex: vertices.get(vertex)) { //получаем вершины в которые есть путь
            if (!used.contains(toVertex)) {
                dfs(toVertex, result, used);
            }
        }

        result.push(vertex);
    }

    public List<BeanVertex> sort() {
        Stack<BeanVertex> result = new Stack<>();
        ArrayList<BeanVertex> used = new ArrayList<BeanVertex>(); //сюда складываем уже посещенные вершины

        for (BeanVertex vertex:vertices.keySet()) {
            if (!used.contains(vertex)) {
                dfs(vertex, result, used);
            }
        }
        return result;
    }
}
