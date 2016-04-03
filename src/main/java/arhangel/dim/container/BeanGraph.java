package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        if (value == null) {
            throw new NullPointerException("null value");
        }
        BeanVertex newVertex = new BeanVertex(value);
        vertices.put(newVertex, new ArrayList<>());
        return newVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        if (from == null || to == null) {
            throw new NullPointerException("null vertex");
        }
        if (!isConnected(from, to)) {
            vertices.get(from).add(to);
        }
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

    /**
     * Топологическая сортировка
     * Предполагаем, что все верщины до начала сортировки белые
     * @return null, если в графе есть цикл, иначе массив отсортированных вершин
     */
    public List<BeanVertex> topSort() {
        List<BeanVertex> sortedList = new ArrayList<>();
        Set<BeanVertex> vertexSet = vertices.keySet();
        for (BeanVertex vertex : vertexSet) {
            if (!dfs(vertex, sortedList)) {
                return null;
            }
        }
        return sortedList;
    }

    /**
     * Обход дерева в глубину, с проверкой на цикличность
      * @param vertex вершина, с которой начинаем обход
     * @return true, если все успешно, false, если встретился цикл
     */
    public boolean dfs(BeanVertex vertex, List<BeanVertex> sortedList) {
        vertex.setColor(BeanVertex.Color.GRAY);
        for (BeanVertex v : getLinked(vertex)) {
            if (v.getColor() == BeanVertex.Color.GRAY) {
                return false;
            }
            if (v.getColor() == BeanVertex.Color.WHITE) {
                dfs(v, sortedList);
            }
        }
        vertex.setColor(BeanVertex.Color.BLACK);
        sortedList.add(vertex);
        return true;
    }

}
