package arhangel.dim.container;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex newVertex = new BeanVertex(value);
        vertices.put(newVertex, new LinkedList<>());
        return newVertex;
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
        return new ArrayList<>(vertices.get(vertex));
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    public List<BeanVertex> sort() throws CycleReferenceException {
        return new GraphSorter().sort();
    }

    private enum VertexColor {
        WHITE, // not visited
        GREY,  // entered, but not finished
        BLACK  // finished
    }

    private class GraphSorter {

        Map<BeanVertex, VertexColor> vertexColor = new HashMap<>();
        List<BeanVertex> sortedVertices = new LinkedList<>();

        private void init() {
            vertexColor.clear();
            sortedVertices.clear();
            for (BeanVertex vertex : vertices.keySet()) {
                vertexColor.put(vertex, VertexColor.WHITE);
            }
        }

        private List<BeanVertex> sort() throws CycleReferenceException {
            init();
            for (BeanVertex vertex : vertices.keySet()) {
                if (vertexColor.get(vertex) == VertexColor.WHITE) {
                    dfs(vertex);
                }
            }
            return new LinkedList<>(sortedVertices);
        }

        private void dfs(BeanVertex vertex) throws CycleReferenceException {
            vertexColor.put(vertex, VertexColor.GREY);
            for (BeanVertex childVertex : getLinked(vertex)) {
                if (vertexColor.get(childVertex) == VertexColor.GREY) {
                    throw new CycleReferenceException("Graph has cycles");
                } else if (vertexColor.get(childVertex) == VertexColor.WHITE) {
                    dfs(childVertex);
                }
            }
            vertexColor.put(vertex, VertexColor.BLACK);
            sortedVertices.add(vertex);
        }


    }

}
