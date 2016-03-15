package rudenko.irina.container;

import rudenko.irina.container.BeanVertex;
import javafx.util.Pair;

import java.util.*;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<rudenko.irina.container.BeanVertex, List<rudenko.irina.container.BeanVertex>> vertices = new HashMap<>();
    private List<rudenko.irina.container.BeanVertex> sortedVertexes = new ArrayList<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public rudenko.irina.container.BeanVertex addVertex(rudenko.irina.container.Bean value)
    {
        rudenko.irina.container.BeanVertex vertex = new rudenko.irina.container.BeanVertex(value);
        vertices.get(vertex);
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(rudenko.irina.container.BeanVertex from , rudenko.irina.container.BeanVertex to) {
        vertices.get(from).add(to);
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(rudenko.irina.container.BeanVertex v1, rudenko.irina.container.BeanVertex v2) {
        return vertices.get(v1).contains(v2);
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<rudenko.irina.container.BeanVertex> getLinked(rudenko.irina.container.BeanVertex vertex) {
        return vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    // проверка на зацикливание
    Map<rudenko.irina.container.BeanVertex, Boolean> used = new HashMap<>();
    Map<rudenko.irina.container.BeanVertex, Boolean> left = new HashMap<>();

    public boolean isCirculed() {
        for (rudenko.irina.container.BeanVertex bv : vertices.keySet()) {
            used.put(bv, false);
            left.put(bv, false);
        }
        for (rudenko.irina.container.BeanVertex bv : vertices.keySet()) {
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
    private void dfs(rudenko.irina.container.BeanVertex tmp) {
        used.put(tmp, true);
        for (rudenko.irina.container.BeanVertex nextBean : getLinked(tmp)) {
            if (!used.get(nextBean)) {
                dfs(nextBean);
            }
        }
        sortedVertexes.add(tmp);
    }

    public List<BeanVertex> topSort(rudenko.irina.container.BeanVertex start) {
        sortedVertexes.clear();
        used.clear();
        for (rudenko.irina.container.BeanVertex bv : vertices.keySet()) {
            used.put(bv, false);
        }
        for (rudenko.irina.container.BeanVertex bv : vertices.keySet()) {
            if (!used.get(bv)) {
                dfs(bv);
            }
        }
        List<BeanVertex> reverseAnswer = new ArrayList<>();
        for (int i = 0; i < size(); ++i) {
            reverseAnswer.add(sortedVertexes.get(i));
        }

        return reverseAnswer;
    }

}
