package ivanov.mikhail.container;

import arhangel.dim.container.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<ivanov.mikhail.container.BeanVertex, List<ivanov.mikhail.container.BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public ivanov.mikhail.container.BeanVertex addVertex(Bean value) {
        return null;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(ivanov.mikhail.container.BeanVertex from , ivanov.mikhail.container.BeanVertex to) {
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(ivanov.mikhail.container.BeanVertex v1, ivanov.mikhail.container.BeanVertex v2) {
        return false;
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<ivanov.mikhail.container.BeanVertex> getLinked(ivanov.mikhail.container.BeanVertex vertex) {
        return null;
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return 0;
    }
}
