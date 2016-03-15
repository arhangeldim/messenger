package arhangel.dim.container;

import javafx.util.Pair;

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
    private List<BeanVertex> sortedVertexes = new ArrayList<>();

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        vertices.get(vertex);
        return vertex;
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
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

    // проверка на зацикливание
    Map<BeanVertex, Boolean> used = new HashMap<>();
    Map<BeanVertex, Boolean> left = new HashMap<>();

    public boolean isCirculed() {
        for (BeanVertex bv : vertices.keySet()) {
            used.put(bv, false);
            left.put(bv, false);
        }
        for (BeanVertex bv : vertices.keySet()) {
            if (!used.get(bv)) {
                if (dfsCheck(bv)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private boolean dfsCheck(BeanVertex bv) {
        used.put(bv, true);
        for (BeanVertex next : getLinked(bv)) {
            if (used.get(next) && !left.get(next)) {
                return true;
            }
            if (!used.get(next) && dfsCheck(next)) {
                return true;
            }
        }
        left.put(bv, true);
        return false;
    }

    private void dfs(BeanVertex tmp) {
        used.put(tmp, true);
        for (BeanVertex nextBean : getLinked(tmp)) {
            if (!used.get(nextBean)) {
                dfs(nextBean);
            }
        }
        sortedVertexes.add(tmp);
    }

    public List<BeanVertex> topSort(BeanVertex start) {
        sortedVertexes.clear();
        used.clear();
        for (BeanVertex tmp : vertices.keySet()) {
            used.put(tmp, false);
        }
        for (BeanVertex tmp : vertices.keySet()) {
            if (!used.get(tmp)) {
                dfs(tmp);
            }
        }
        ArrayList<BeanVertex> reverseAnswer = new ArrayList<>();
        for (int i = 0; i < size(); ++i) {
            reverseAnswer.add(sortedVertexes.get(i));
        }

        return reverseAnswer;
    }

}
