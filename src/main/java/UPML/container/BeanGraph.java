package UPML.container;

import arhangel.dim.container.Bean;

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
        BeanVertex beanVertex = new BeanVertex(value);
        if(vertices.containsKey(beanVertex)) {
            return beanVertex;
        }
        //Непонятно пока, как понормальному добавить вершины=(
        vertices.put(beanVertex, null);
        return beanVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
        //можно ли просто добавлять в лист не перезаписывая?
        List<BeanVertex> beanVertexes = getLinked(from);
        if(beanVertexes.contains(to)) {
            return;
        }
        beanVertexes.add(to);
        vertices.replace(from, beanVertexes);
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
}
