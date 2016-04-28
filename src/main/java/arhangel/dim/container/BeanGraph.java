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

    public Map<BeanVertex, List<BeanVertex>> getVertexMap() {
        return vertices;
    }

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex newVertex = new BeanVertex(value);
        vertices.put(newVertex, new ArrayList<>());
        return newVertex;
    }

    public void updateGraphLinks() {
        for (BeanVertex currentBeanVertex : vertices.keySet()) {

            if (currentBeanVertex.getBean().getProperties() == null) {
                continue;
            }
            for (BeanVertex destinationBeanVertex : vertices.keySet()) {
                if (destinationBeanVertex.getBean().getProperties() == null ||
                        destinationBeanVertex.equals(currentBeanVertex)) {
                    continue;
                }

                for (Property currentProperty : destinationBeanVertex.getBean().getProperties().values()) {
                    if ((currentProperty.getType() == ValueType.REF) &&
                            currentProperty.getValue().equals(currentBeanVertex.getBean().getName())) {
                        addEdge(destinationBeanVertex, currentBeanVertex);
                    }
                }
            }
        }
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        if (!vertices.get(from).contains(to)) {
            vertices.get(from).add(to);
        }
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


    //Фичи для графов:
    private Map<BeanVertex, Boolean> used = new HashMap<BeanVertex, Boolean>();
    private Map<BeanVertex, Boolean> left = new HashMap<BeanVertex, Boolean>();
    private List<BeanVertex> sortedVertexes = new ArrayList<BeanVertex>();

    public List<BeanVertex> getSortedVertexes() throws InvalidConfigurationException {


        if (isCorrect()) {
            return topSort();
        } else {
            throw new InvalidConfigurationException("Ошибка конфигурации: Граф загрузки цикличен!");
        }
    }

    public boolean isCorrect() {
        for (BeanVertex beanVertex : vertices.keySet()) {
            used.put(beanVertex, false);
            left.put(beanVertex, false);
        }

        for (BeanVertex beanVertex : vertices.keySet()) {
            if (!used.get(beanVertex)) {
                if (dfsCheck(beanVertex)) {
                    return false;
                }
            }
            return true;
        }
        return true;

    }


    private boolean dfsCheck(BeanVertex beanVertex) {
        used.put(beanVertex, true);
        for (BeanVertex next : getLinked(beanVertex)) {
            if (used.get(next) && !left.get(next)) {
                return true;
            }
            if (!used.get(next) && dfsCheck(next)) {
                return true;
            }
        }
        left.put(beanVertex, true);
        return false;
    }


    private void dfs(BeanVertex beanVertex) {
        used.put(beanVertex, true);
        for (BeanVertex nextBean : getLinked(beanVertex)) {
            if (!used.get(nextBean)) {
                dfs(nextBean);
            }
        }
        sortedVertexes.add(beanVertex);
    }


    public List<BeanVertex> topSort() {
        sortedVertexes.clear();
        used.clear();
        for (BeanVertex beanVertex : vertices.keySet()) {
            used.put(beanVertex, false);
        }
        for (BeanVertex beanVertex : vertices.keySet()) {
            if (!used.get(beanVertex)) {
                dfs(beanVertex);
            }
        }

        return sortedVertexes;
    }

}
