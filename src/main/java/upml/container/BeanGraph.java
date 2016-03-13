package upml.container;

import arhangel.dim.container.Bean;
import arhangel.dim.container.Property;
import arhangel.dim.container.ValueType;

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
    private Map<BeanVertex, Boolean> used;
    private Map<BeanVertex, Boolean> left;  //вершины из которых мы вышли в dfs
    private List<BeanVertex> answerForSort;

    /**
     * Добавить вершину в граф
     *
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        BeanVertex beanVertex = new BeanVertex(value);
        if (vertices.containsKey(beanVertex)) {
            return beanVertex;
        }
        vertices.put(beanVertex, new ArrayList<>());

        //добавляем ребра в новую вершину
        for (BeanVertex tmp : vertices.keySet()) {
            for (Property property : tmp.getBean().getProperties().values()) {
                if (property.getType() == ValueType.REF && property.getValue().equals(value.getName())) {
                    addEdge(tmp, beanVertex);
                }
            }
        }


        //добавляем ребра из новой вершины
        for (Property property : value.getProperties().values()) {
            if (property.getType() == ValueType.REF) {
                for (BeanVertex tmp : vertices.keySet()) {
                    if (tmp.getBean().getName().equals(property.getName())) {
                        addEdge(beanVertex, tmp);
                    }
                }
            }
        }
        return beanVertex;
    }

    /**
     * Соединить вершины ребром
     *
     * @param from из какой вершины
     * @param to   в какую вершину
     */
    public void addEdge(BeanVertex from, BeanVertex to) {
        //можно ли просто добавлять в лист не перезаписывая?
        List<BeanVertex> beanVertexes = getLinked(from);
        if (beanVertexes.contains(to)) {
            return;
        }
        beanVertexes.add(to);
        vertices.replace(from, beanVertexes);
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

    public boolean checkCycle() {
        used = new HashMap<>();
        left = new HashMap<>();
        for (BeanVertex tmp : vertices.keySet()) {
            used.put(tmp, false);
            left.put(tmp, false);
        }
        for (BeanVertex tmp : vertices.keySet()) {
            if (!used.get(tmp)) {
                if (dfsCheck(tmp)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<BeanVertex> topSort(BeanVertex start) {
        answerForSort = new ArrayList<>();
        used = new HashMap<>();
        for (BeanVertex tmp : vertices.keySet()) {
            used.put(tmp, false);
        }
        for (BeanVertex tmp : vertices.keySet()) {
            if (!used.get(tmp)) {
                dfs(tmp);
            }
        }
        ArrayList<BeanVertex> reverseAnswer = new ArrayList<>();
        for (int i = 0; i < size(); ++i) {
            reverseAnswer.add(answerForSort.get(i));
        }

        return reverseAnswer;
    }

    private void dfs(BeanVertex tmp) {
        used.put(tmp, true);
        for (BeanVertex nextBean : getLinked(tmp)) {
            if (!used.get(nextBean)) {
                dfs(nextBean);
            }
        }
        answerForSort.add(tmp);
    }


    private boolean dfsCheck(BeanVertex tmp) {
        used.put(tmp, true);
        for (BeanVertex nextBean : getLinked(tmp)) {
            if (used.get(nextBean) && !left.get(nextBean)) {
                return true;
            }
            if (!used.get(nextBean) && dfsCheck(nextBean)) {
                return true;
            }
        }
        left.put(tmp, true);
        return false;
    }
}
