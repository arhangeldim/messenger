package arhangel.dim.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class BeanGraph {

    private static Logger log = LoggerFactory.getLogger(BeanGraph.class);

    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        vertices.put(vertex, new ArrayList<>());
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from , BeanVertex to) throws Exception {
        if (from != null && to != null && vertices.containsKey(from)) {
            vertices.get(from).add(to);
        } else {
            throw new InvalidConfigurationException("Error in addEdge");
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

    void dfs(BeanVertex current) throws Exception {
        current.setState(BeanVertex.State.MARKED);
        for (BeanVertex vertex : getLinked(current)) {
            //log.info("ADD SORT LIST " + vertex.getBean().getName());
            if (vertex.getState() == BeanVertex.State.MARKED) {
                throw new CycleReferenceException("Cycle in your ref dependency");
            }
            if (vertex.getState() == BeanVertex.State.DEFAULT) {
                dfs(vertex);
            }
        }
        current.setState(BeanVertex.State.VISITED);
        sorted.add(current.getBean());
    }

    private List<Bean> sorted = new LinkedList<>();

    public List<Bean> topSort() throws Exception {
        for (BeanVertex vertex : vertices.keySet()) {
            if (vertex.getState() == BeanVertex.State.DEFAULT) {
                //log.info("ADD SORT LIST " + vertex.getBean().getName());
                dfs(vertex);
            }
        }

        return sorted;
    }

}
