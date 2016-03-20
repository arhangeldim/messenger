package arhangel.dim.container;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    // изменил лист на сет, т.к. отпадает проверка уже существующего ребра при добавление нового
    private Map<BeanVertex, Set<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex beanVertex = new BeanVertex(value);
        if (vertices.containsKey(beanVertex)) {
            throw new IllegalStateException(value + " have already been in the graph");
        }
        vertices.put(beanVertex, new HashSet<>());
        return beanVertex;
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
        if (vertices.containsKey(v1)) {
            return vertices.get(v1).contains(v2);
        } else {
            return false;
        }
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public Iterable<BeanVertex> getLinked(BeanVertex vertex) {
        return vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    public void parseBean(Bean bean) {

    }

    private List<Bean> orderedResult;

    public List<Bean> getOrderedBeans() throws CycleReferenceException {
        if (orderedResult == null) {
            TopologicalSorter sorter = new TopologicalSorter();
            if (!sorter.sort()) {
                throw new CycleReferenceException("A cycle was found");
            }

            orderedResult = sorter.result.stream().map(beanVertex -> beanVertex.getBean()).collect(Collectors.toList());
        }
        return orderedResult;
    }


    private class TopologicalSorter {
        Map<BeanVertex, Integer> color;
        List<BeanVertex> result;

        TopologicalSorter() {  }

        /**
         * Поиск в глубину
         * @param vertex do dfs from
         * @return true if cycle was found
         */
        Boolean dfs(BeanVertex vertex) {
            if (color.get(vertex) == 1) {
                return true; // cycle
            }
            if (color.get(vertex) == 2) {
                return false;
            }
            color.put(vertex, 1);
            for (BeanVertex bv: getLinked(vertex)) {
                if (dfs(bv)) {
                    return true;
                }
            }
            result.add(vertex);
            color.put(vertex, 2);
            return false;
        }

        /**
         *
         * @return false if cycle was found
         */
        boolean sort() {
            color = vertices.keySet().stream().collect(Collectors.toMap(beanVertex -> beanVertex, beanVertex1 -> 0));
            result = new ArrayList<>(size());

            for (BeanVertex vertex: vertices.keySet()) {
                if (dfs(vertex)) {
                    return false;
                }
            }

            //Collections.reverse(result);
            return true;
        }

    }


}
