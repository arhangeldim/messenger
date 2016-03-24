package arhangel.dim.container;

import sun.security.provider.certpath.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import java.util.* ;

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
        BeanVertex beanVertex = new BeanVertex( value );
        vertices.put(beanVertex, new ArrayList());
        return beanVertex;
    }

    /**
     * Соединить вершины ребром
     * @param from из какой вершины
     * @param to в какую вершину
     */
    public void addEdge(BeanVertex from ,BeanVertex to) {
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

    public void removeEdge(BeanVertex from, BeanVertex to) {
        if (vertices.get(from).contains(to)) {
            vertices.get(from).remove(to);
        }
    }



    public List<BeanVertex> sort(List<Bean> unsortedBeans) throws CycleReferenceException {
        List<BeanVertex> sortedBeanVertex = new ArrayList<>();

        for ( Bean bean : unsortedBeans ) {
            addVertex( bean );
        }

        for ( BeanVertex beanVertex : vertices.keySet()) {
            for ( Property property : beanVertex.getBean().getProperties().values()) {
                if ( property.getType() == ValueType.REF ) {
                    String referencedBeanName = property.getValue();
                    for ( BeanVertex beanVertex1 : vertices.keySet()) {
                        if (beanVertex1.getBean().getName().equals(referencedBeanName)) {
                            addEdge(beanVertex,beanVertex1);
                        }
                    }
                }
            }
        }
        int counter = 0;
        do {
            counter = size();
            for (Iterator<BeanVertex> i = vertices.keySet().iterator(); i.hasNext(); ) {
                BeanVertex beanVertex = i.next();
                if (vertices.get(beanVertex).size() == 0) {
                    for (BeanVertex beanVertex1 : vertices.keySet()) {
                        removeEdge(beanVertex1, beanVertex);
                    }
                    sortedBeanVertex.add(beanVertex);
                    i.remove();
                }
            }
        } while (size() != 0 && counter != size() );
        if ( size() != 0 ) {
            throw new CycleReferenceException("ERROR");
        }



        return sortedBeanVertex;
    }




}
