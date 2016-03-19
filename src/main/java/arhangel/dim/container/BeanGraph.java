package arhangel.dim.container;

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
    private Map<String, BeanVertex> namesToVertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex new_vertex = new BeanVertex(value);

        vertices.put(new_vertex, new ArrayList<BeanVertex>());
        namesToVertices.put(value.getName(), new_vertex);

        return new_vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
        List<BeanVertex> incidentVertices = vertices.get(from);
        incidentVertices.add(to);
    }

    public BeanGraph(List<Bean> beans) {
        // adding all the vertices
        for (Bean bean : beans) {
            addVertex(bean);
        }

        // adding edges between vertices
        for (Bean bean : beans) {
            BeanVertex from = namesToVertices.get(bean.getName());

            HashMap<String, Property> properties = (HashMap<String, Property>) bean.getProperties();
            for (Property property : properties.values()) {

                if (property.getType() == ValueType.VAL) {
                    continue;
                }

                BeanVertex to = namesToVertices.get(property.getName());
                addEdge(from, to);
            }
        }
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        return false;
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {
        return null;
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    public boolean DFS()
}
