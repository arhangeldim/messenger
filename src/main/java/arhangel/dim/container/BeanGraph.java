package arhangel.dim.container;

import java.util.*;

/**
 *
 */
public class BeanGraph {
    // Граф представлен в виде списка связности для каждой вершины
    public Map<BeanVertex, List<BeanVertex>> vertices = new HashMap<>();
    public List<BeanVertex> graphVertexList = new ArrayList<>();
    public List<Bean> beanListSorted = new ArrayList<>();

    public BeanGraph() {};
    public BeanGraph(List<Bean> beanList){

        this.setGraphVertexList(beanList);
        this.connectGraph();
    }
    /**
     * Добавить вершину в граф
     * @param value - объект, привязанный к вершине
     */
    public BeanVertex addVertex(Bean value) {
        return null;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {

    }

    /**
     * Проверяем, связаны ли вершины
     */
    public boolean isConnected(BeanVertex v1, BeanVertex v2) {
        boolean bool = false;
            if (vertices.get(v1) != null) {
                if (vertices.get(v1).contains(v2)) {
                    bool = true;
                }
                else {
                    bool = false;
                }
        }
        else bool = false;
        return bool;
    }


    private void setGraphVertexList(List<Bean> beanList) {
        for (int i = 0; i < beanList.size(); i++) {
            this.graphVertexList.add(new BeanVertex(beanList.get(i)));
        }
    }

    /**
     * Получить список вершин, с которыми связана vertex
     */
    public List<BeanVertex> getLinked(BeanVertex vertex) {   //List<BeanVertex>

        List<BeanVertex> refToList = new ArrayList<>();
        List<Property> propertyCurrList = new ArrayList<>(vertex.getBean().getProperties().values());                   //список полей vertex

        for (int i = 0; i < propertyCurrList.size(); i++) {
            if (propertyCurrList.get(i).getType().equals(ValueType.REF))

                for (int j = 0; j < graphVertexList.size(); j++) {
                    if (propertyCurrList.get(i).getValue().equals(graphVertexList.get(j).getBean().getName())) {
                        refToList.add(graphVertexList.get(j));
                    }
                }
        }

        return refToList;
    }

    /**
     * Количество вершин в графе
     */
    public int size() {
        return 0;
    }

    //Определяем для каждой вершины, на какие вершины он ссылается
    public void connectGraph() {
        for (int i = 0; i < graphVertexList.size(); i++) {

            List<BeanVertex> beanVertexList = this.getLinked(graphVertexList.get(i));
            vertices.put(graphVertexList.get(i), beanVertexList);
        //    if (vertices.get(graphVertexList.get(i)) != null && !beanVertexList.isEmpty())
        }
    }

    public void printGraph() {
        System.out.println("All vertices:\n");
        for (int i = 0; i < graphVertexList.size(); i++) {

            System.out.println(graphVertexList.get(i).toString());
            if (!vertices.get(graphVertexList.get(i)).isEmpty()) {
                System.out.println("This vertice's refferencies:\n" + vertices.get(graphVertexList.get(i)).toString() + "\n\n");
            }
            else {
                System.out.println("No refferencies\n");
            }
        }
    }

    public boolean deep_search(BeanVertex bean) {
        if (bean.getSearch_check() == 1) return true;
        if (bean.getSearch_check() == 2) return false;
        bean.setSearch_check(1);

        for (int i = 0; i < vertices.get(bean).size(); i++) {
            if (deep_search(vertices.get(bean).get(i))) return true;
        }
        beanListSorted.add(bean.getBean());
        bean.setSearch_check(2);
        return false;
    }

    public List<Bean> sortBeans() {
        boolean cyclic;
        for( int i = 0; i < graphVertexList.size(); i++) {
            cyclic = deep_search(graphVertexList.get(i));
            if (cyclic) return null;
        }
        return beanListSorted;
    }




}
