package arhangel.dim.container;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    private Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();

    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex newBeanVertex = new BeanVertex(value);
        vertices.put(newBeanVertex, new ArrayList<>());

        //Добавляем ребра из существующих вершин в новое
        for (BeanVertex beanVertex: vertices.keySet()) {
            Bean bean = beanVertex.getBean();
            for (String name: bean.getProperties().keySet()) {
                if (bean.getProperties().get(name).getType() == ValueType.REF &&
                        bean.getProperties().get(name).getValue().equals(value.getName())) {
                    addEdge(beanVertex, newBeanVertex);
                }
            }
        }
        //Добавляем ребра из новой вершины в существующие
        for (String newName: value.getProperties().keySet()) {
            if (value.getProperties().get(newName).getType() == ValueType.REF) {
                for (BeanVertex beanVertex: vertices.keySet()) {
                    if (beanVertex.getBean().getName().equals(value.getProperties().get(newName).getValue())) {
                        addEdge(newBeanVertex, beanVertex);
                    }
                }
            }
        }
        return newBeanVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
        List<BeanVertex> edges = vertices.get(from);
        if (!(edges.contains(to))) {
            edges.add(to);
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
        return vertices.size()  ;
    }

    private void dfs(BeanVertex beanVertex, Map<BeanVertex, Boolean> used, Queue<BeanVertex> sorted) {
        used.put(beanVertex, true);
        for (BeanVertex mate: vertices.get(beanVertex)) {
            if (!used.get(mate)) {
                dfs(mate, used, sorted);
            }
        }
        sorted.add(beanVertex);
    }

    public List<BeanVertex> getSortedList() {
        Queue<BeanVertex> sorted = new LinkedList<>();
        Map<BeanVertex, Boolean> used = new HashMap<>();
        for (BeanVertex beanVertex: vertices.keySet()) {
            used.put(beanVertex, false);
        }
        for (BeanVertex beanVertex: vertices.keySet()) {
            if (!used.get(beanVertex)) {
                dfs(beanVertex, used, sorted);
            }
        }
        List<BeanVertex> result = new ArrayList<>();
        while (!sorted.isEmpty()) {
            result.add(sorted.poll());
        }
        return result;
    }

    public boolean isAcyclic() {
        Map<BeanVertex, Boolean> used;
        for (BeanVertex beanVertex: vertices.keySet()) {
            used = new HashMap<>();
            for (BeanVertex bv: vertices.keySet()) {
                used.put(bv, false);
            }
            if (!isAcyclic(beanVertex, used)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAcyclic(BeanVertex beanVertex, Map<BeanVertex, Boolean> used) {
        used.put(beanVertex, true);
        for (BeanVertex mate: vertices.get(beanVertex)) {
            if (used.get(mate) || (!isAcyclic(mate, used))) {
                return false;
            }
        }
        return true;
    }
}
