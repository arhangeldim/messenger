package arhangel.dim.container;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<BeanVertex> vertices = new ArrayList<>();
        BeanGraph graph = new BeanGraph();
        List<Bean> beans = (new BeanXmlReader()).parseBeans("test.xml");

        for (Bean bean : beans) {
            vertices.add(graph.addVertex(bean));
        }
        for (BeanVertex parentVertex : vertices) {
            for (BeanVertex childVertex : vertices) {
                if (!parentVertex.equals(childVertex) && graph.isConnected(parentVertex, childVertex)) {
                    graph.addEdge(parentVertex, childVertex);
                    System.out.println(parentVertex.getBean().getName() + " - " + childVertex.getBean().getName());
                }
            }
        }
        try {
            List<BeanVertex> list = graph.sort();
            for (BeanVertex beanVertex : list) {
                System.out.println(beanVertex.getBean().toString());
            }
        } catch (CycleReferenceException e) {
            System.out.println(e.getMessage());
        }
    }
}