package arhangel.dim.container;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    // This Map provides BeanVertex instance by Bean.name
    private Map<String, BeanVertex> vertexNames = new HashMap<>();

    // This Map contains marks if vertex is already visited by dfs.
    // 1 means vertex visited, 0 means not visited and -1 means vertex is currently processed
    private Map<BeanVertex, Integer> used = new HashMap<>();

    // This array will needed for topsort. Contains vertices in reversed out time order
    private ArrayList<BeanVertex> answer = new ArrayList<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex beanVertex = new BeanVertex(value);
        vertices.put(beanVertex, new ArrayList<>());
        vertexNames.put(value.getName(), beanVertex);
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

    /**
     * Returnes BeanVertex instance by given Bean name
     */
    private BeanVertex getVertexByName(String name) {
        return vertexNames.get(name);
    }

    /**
     * Connects all vertices stored in this.vertices.
     * To work properly for this method all graph vertices need to be stored in this.vertices.
     */
    private void createGraph() {
        for (BeanVertex vertex :
                vertices.keySet()) {
            vertices.get(vertex).clear();
            for (Property property :
                    vertex.getBean().getProperties().values()) {
                if (property.getType() == ValueType.REF) {
                    addEdge(vertex, getVertexByName(property.getValue()));
                }
            }
        }
    }


    /**
     * Runs depth first search.
     * @param vertex The start vertex for the dfs
     * @return true if graph contains cycle, false otherwise,
     */
    private boolean dfs(BeanVertex vertex) {
        boolean res = false;
        used.put(vertex,0);
        for (BeanVertex next :
                vertices.get(vertex)) {
            if (used.get(next) == 0) {
                if (dfs(next)) {
                    res = true;
                }
            }
            if (used.get(next) == -1) {
                res = true;
            }
        }
        answer.add(vertex);
        return res;
    }

    /**
     * Checks graph for cycles content.
     * @return true if there is cycle in the graph, false otherwise
     */
    public boolean checkForCycle() {
        boolean contains = false;
        createGraph();
        used.clear();
        for (BeanVertex vertex :
                vertices.keySet()) {
            used.put(vertex, 0);
        }

        for (BeanVertex vertex :
                vertices.keySet()) {
            if (used.get(vertex) == 0 && dfs(vertex)) {
                contains = true;
            }
        }
        return contains;
    }

    /**
     * Sorts graph in topological order.
     * @return List of ordered vertices.
     */
    public List<BeanVertex> sort() {
        createGraph();
        used.clear();
        for (BeanVertex vertex :
                vertices.keySet()) {
            used.put(vertex, 0);
        }

        answer.clear();
        for (BeanVertex vertex :
                vertices.keySet()) {
            if (used.get(vertex) == 0) {
                dfs(vertex);
            }
        }
        Collections.reverse(answer);
        return answer;
    }
}
