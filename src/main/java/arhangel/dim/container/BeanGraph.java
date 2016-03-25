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

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex vertex = new BeanVertex(value);
        vertices.put(vertex, new ArrayList<>());
        return vertex;
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

    public void addAllEdges(Bean rootBean) {
        BeanVertex rootVertex = addVertex(rootBean);
        // набор полей бина имя поля - значение
        Map<String, Property> mergeMap = rootVertex.getBean().getProperties();
        for (String nameProperty : mergeMap.keySet()) {
            if (mergeMap.get(nameProperty).getType() == ValueType.REF) {
                String nameBeanVertex = mergeMap.get(nameProperty).getValue();
                for (BeanVertex vertex : vertices.keySet()) {
                    if (vertex.getBean().getName() == nameBeanVertex) {
                        addEdge(rootVertex, vertex);
                    }
                }
            }
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

    private HashMap<BeanVertex, Color> used = new HashMap<>();
    private ArrayList<BeanVertex> sortList = new ArrayList<>();

    public ArrayList<BeanVertex> topologicalSort() throws InvalidConfigurationException, CycleReferenceException {
        ArrayList<BeanVertex> sortList = new ArrayList<>();
        for (BeanVertex vertex : used.keySet()) {
            used.put(vertex, Color.WRITE);
        }
        for (BeanVertex vertex : used.keySet()) {
            dfs(vertex);
        }
        return sortList;
    }

    public void dfs(BeanVertex vertex) throws InvalidConfigurationException, CycleReferenceException {
        if (used.get(vertex) == Color.WRITE) {
            used.replace(vertex, Color.WRITE, Color.GREY);
            ArrayList<BeanVertex> linkedList = (ArrayList<BeanVertex>) getLinked(vertex);
            for (BeanVertex mergesVertex : linkedList) {
                dfs(mergesVertex);
            }
            sortList.add(vertex);
            used.replace(vertex, Color.BLACK);
        } else {
            if (used.get(vertex) == Color.GREY) {
                throw new CycleReferenceException(String.format("Ошибка XML:" +
                        "существует циклическая зависимость у %s", vertex.getBean().getName()));
            }


        }
    }
}
