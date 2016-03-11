package arhangel.dim.container.dag;

import java.util.List;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Graph<Integer> graph = new Graph<>();
        Vertex<Integer> v1 = graph.addVertex(1);
        Vertex v2 = graph.addVertex(2);
        Vertex v3 = graph.addVertex(3);
        Vertex v4 = graph.addVertex(4);
        Vertex v5 = graph.addVertex(5);

        Vertex v6 = graph.addVertex(6);
        Vertex v7 = graph.addVertex(7);


        graph.addEdge(v1, v2, true);
        graph.addEdge(v1, v3, true);
        graph.addEdge(v2, v3, true);
        graph.addEdge(v2, v4, true);
        graph.addEdge(v3, v5, true);
        graph.addEdge(v4, v5, true);

        graph.addEdge(v6, v7, true);

        List<Vertex<Integer>> sorted = graph.toposort();

    }

}
