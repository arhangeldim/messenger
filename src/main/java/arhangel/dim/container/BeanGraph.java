package arhangel.dim.container;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class BeanGraph {



    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex newBeanVertex = new BeanVertex(value);
        List<BeanVertex> edges = new LinkedList<>();
        if (!vertices.containsKey(newBeanVertex)) {
            vertices.put(newBeanVertex, edges);
        }
        return newBeanVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from,BeanVertex to) {
        List<BeanVertex> edges = vertices.get(from);
        edges.add(to);

    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        List<BeanVertex> edges1 = vertices.get(v1);
        List<BeanVertex> edges2 = vertices.get(v1);
        return (edges1.contains(v2) || edges2.contains(v1));
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {
        List<BeanVertex> edges = vertices.get(vertex);
        return edges;
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    public void dfs(BeanVertex start,  List<BeanVertex> sortedVertices, Map<BeanVertex, Integer> colouredVertices)
            throws CycleReferenceException {
        List<BeanVertex> edges = vertices.get(start);
        for (BeanVertex child : edges) {
            if (colouredVertices.containsKey(child) && colouredVertices.get(child) == 0) {
                throw new CycleReferenceException("cycle found");
            }
            if (!sortedVertices.contains(child)) {
                sortedVertices.add(child);
                try {
                    colouredVertices.put(child, 0);
                    dfs(child, sortedVertices, colouredVertices);
                    colouredVertices.put(child, 1);
                } catch (CycleReferenceException e) {
                    throw e;
                }
            }
        }
    }

    public List<BeanVertex> sort() throws CycleReferenceException {
        List<BeanVertex> sortedVertices = new LinkedList<>();
        Map<BeanVertex, Integer> colouredVertices = new HashMap<>();
        Iterator it = vertices.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry vertex = (Map.Entry) it.next();
            BeanVertex beanVertex = (BeanVertex) (vertex.getKey());
            if (colouredVertices.containsKey(beanVertex) && colouredVertices.get(beanVertex) == 0) {
                throw new CycleReferenceException("cycle found");
            }
            if (!sortedVertices.contains(beanVertex)) {
                try {
                    colouredVertices.put(beanVertex, 0);
                    dfs(beanVertex, sortedVertices, colouredVertices);
                    colouredVertices.put(beanVertex, 1);
                } catch (CycleReferenceException e) {
                    throw e;
                }
            }
            sortedVertices.add(beanVertex);
        }
        return sortedVertices;
    }
}
