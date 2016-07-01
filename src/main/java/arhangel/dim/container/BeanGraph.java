package arhangel.dim.container;

import arhangel.dim.container.exceptions.CycleReferenceException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static java.util.Collections.reverse;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();
    private Map<String, BeanVertex> vertexByName = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex newVertex = new BeanVertex(value);

        vertices.put(newVertex, new ArrayList<BeanVertex>());
        vertexByName.put(value.getName(), newVertex);

        return newVertex;
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

    public BeanGraph() {}

    public BeanGraph(List<Bean> beans) {
        // adding all the vertices
        for (Bean bean : beans) {
            addVertex(bean);
        }

        // adding edges between vertices
        for (Bean bean : beans) {
            BeanVertex from = vertexByName.get(bean.getName());

            HashMap<String, Property> properties = (HashMap<String, Property>) bean.getProperties();
            for (Property property : properties.values()) {

                if (property.getType() == ValueType.VAL) {
                    continue;
                }

                BeanVertex to = vertexByName.get(property.getName());
                addEdge(from, to);
            }
        }
    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        List<BeanVertex> incidentVertices = vertices.get(v1);
        return incidentVertices.contains(v2);
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

    private enum VertexType {
        NOT_PROCESSED, // dfs в вершину еще не заходил
        STARTED_PROCESSING, // dfs зашел в вершину
        FINISHED_PROCESSING // dfs вышел из вершины
    }

    /**
     * Проверить граф на наличие циклов
     */
    private boolean isCircle(BeanVertex vertex, List<BeanVertex> sortedVertices,
                             Map<BeanVertex, VertexType> usedVertices) {
        usedVertices.put(vertex, VertexType.STARTED_PROCESSING);

        for (BeanVertex incidentVertex : vertices.get(vertex)) {
            if (usedVertices.get(incidentVertex).equals(VertexType.NOT_PROCESSED)) {
                isCircle(incidentVertex, sortedVertices, usedVertices);
            } else if (usedVertices.get(incidentVertex).equals(VertexType.STARTED_PROCESSING)) {
                return true;
            }
        }
        sortedVertices.add(vertex);
        usedVertices.put(vertex, VertexType.FINISHED_PROCESSING);
        return false;
    }

    /**
     * Отсортировать вершины графа в топологическом порядке
     */
    public List<BeanVertex> sortTopologically() throws CycleReferenceException {
        Map<BeanVertex, VertexType> usedVertices = new HashMap<>();
        for (BeanVertex vertex : vertices.keySet()) {
            usedVertices.put(vertex, VertexType.NOT_PROCESSED);
        }
        List<BeanVertex> sortedVertices = new ArrayList<>();

        for (BeanVertex vertex : vertices.keySet()) {
            if (!usedVertices.get(vertex).equals(0)) {
                continue;
            }

            boolean foundCircle = isCircle(vertex, sortedVertices, usedVertices);
            if (foundCircle) {
                throw new CycleReferenceException("circle reference found");
            }

        }
        return sortedVertices;
    }
}
