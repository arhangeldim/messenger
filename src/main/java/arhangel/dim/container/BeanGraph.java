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
    private Map<BeanVertex, Integer> colors = new HashMap<>();
    private List<BeanVertex> sortedVertices = new ArrayList<>();
    private int order;

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        List<BeanVertex> connectedsortedVertices = new ArrayList<>();
        if (!vertices.containsKey(vertex)) {
            vertices.put(vertex, connectedsortedVertices);
            colors.put(vertex, 0);
        }
        return vertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
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
        return vertices.get(vertex);
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return vertices.size();
    }

    private void sort( ) throws InvalidConfigurationException {
        for (BeanVertex vertex : vertices.keySet()) {

            if (colors.get(vertex) == 0) {

                dfs(vertex);
            }
        }
    }

    public void dfs(BeanVertex vertex) throws InvalidConfigurationException {

        List<BeanVertex> linkedVertices = getLinked(vertex);
        
        colors.replace(vertex, 1);

        for (int v = 0; v < linkedVertices.size(); v++) {

            if (colors.get(linkedVertices.get(v)) == 0) {

                dfs(linkedVertices.get(v));
            }

            if (colors.get(linkedVertices.get(v)) == 1) {

                throw new InvalidConfigurationException("There is a cycle in config.xml");
            }
        }

        colors.replace(vertex, 2);
        sortedVertices.add(vertex);
        order ++;
        
    }

    public void setGraph(List<Bean> beanList) throws InvalidConfigurationException {
        for (Bean bean : beanList) {
            BeanVertex vertex = addVertex(bean);
        }
        
        for (BeanVertex vertex : vertices.keySet()) {
            for (Property property : vertex.getBean().getProperties().values()) {

                if (property.getType() == ValueType.REF) {

                    boolean isFound = false;
                    for (BeanVertex verticesToCheck : vertices.keySet()) {

                        if (verticesToCheck.getBean().getName().matches(property.getValue())) {
                            if (isFound) {

                                throw new InvalidConfigurationException("There should be only one Bean with name " + property.getName());
                            }
                            isFound = true;
                            addEdge(vertex, verticesToCheck);
                        }
                    }
                }
            }
        }
    }

    public  List<Bean> sortedBeanList(List<Bean> beansToSort) throws InvalidConfigurationException {
        setGraph(beansToSort);
        order = 0;
        sort();
        List<Bean> sortedBeans = new ArrayList<>();
        
        for (int b = 0; b < sortedVertices.size(); b++) {
            sortedBeans.add(sortedVertices.get(b).getBean());
        }

        return sortedBeans;
    }
}
