package arhangel.dim.container;

import java.util.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.Assert;

/**
 *
 */
public class BeanGraphTest {

    private BeanGraph graph;
    private List<BeanVertex> vertices;

    @Before
    public void initTest() {
        graph = new BeanGraph();
        BeanVertex v0 = graph.addVertex(new Bean("0", "class", new HashMap<String, Property>()));
        BeanVertex v1 = graph.addVertex(new Bean("1", "class", new HashMap<String, Property>()));
        BeanVertex v2 = graph.addVertex(new Bean("2", "class", new HashMap<String, Property>()));
        BeanVertex v3 = graph.addVertex(new Bean("3", "class", new HashMap<String, Property>()));

        vertices = new ArrayList<>();
        vertices.addAll(Arrays.asList(v0, v1, v2, v3));

        graph.addEdge(v0, v1);
        graph.addEdge(v0, v2);
        graph.addEdge(v1, v3);
    }

    @Test
    @Ignore
    public void testIsConnected() throws Exception {
        Assert.assertTrue(graph.isConnected(vertices.get(0), vertices.get(1)));
        Assert.assertFalse(graph.isConnected(vertices.get(0), vertices.get(3)));
    }

    @Test
//    @Ignore
    public void testGetLinked() throws Exception {
        BeanVertex[] linked = new BeanVertex[]{vertices.get(1), vertices.get(2)};
        int counter = 0;
        for (BeanVertex vertex : graph.getLinked(vertices.get(0))) {
            Assert.assertEquals(linked[counter++], vertex);
        }
    }

//    @Test
//    @Ignore
//    public void testSort() throws Exception {
////        List<BeanVertex> result = this.graph.sort();
//        BeanVertex[] exp = new BeanVertex[]{
//                vertices.get(3),
//                vertices.get(1),
//                vertices.get(2),
//                vertices.get(0)
//        };
//        int counter=0;
//        for (BeanVertex vertex : this.graph.sort()) {
//            System.out.println("vertex: "+vertex);
//            Assert.assertEquals(exp[counter++], vertex);
//        }
//    }

    @Test
//    @Ignore
    public void testSize() throws Exception {
        Assert.assertEquals(4, graph.size());
    }

//    @Test
////    @Ignore
//    public void testSort() {
//        Assert.assertNotNull(graph.sort());
////        graph.addEdge(vertices.get(1), vertices.get(0));
////        Assert.assertNull(graph.sort());
//    }
}
