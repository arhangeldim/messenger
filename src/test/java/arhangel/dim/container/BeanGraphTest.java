package arhangel.dim.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        BeanVertex v0 = graph.addVertex(new Bean("0", null, null));
        BeanVertex v1 = graph.addVertex(new Bean("1", null, null));
        BeanVertex v2 = graph.addVertex(new Bean("2", null, null));
        BeanVertex v3 = graph.addVertex(new Bean("3", null, null));

        vertices = new ArrayList<>();
        vertices.addAll(Arrays.asList(v0, v1, v2, v3));

        graph.addEdge(v0, v1);
        graph.addEdge(v0, v2);
        graph.addEdge(v1, v3);
    }

    @Test
    public void testIsConnected() throws Exception {
        Assert.assertTrue(graph.isConnected(vertices.get(0), vertices.get(1)));
        Assert.assertFalse(graph.isConnected(vertices.get(0), vertices.get(3)));
    }

    @Test
    public void testGetLinked() throws Exception {
        BeanVertex[] linked = new BeanVertex[]{vertices.get(1), vertices.get(2)};
        int counter = 0;
        for (BeanVertex vertex : graph.getLinked(vertices.get(0))) {
            Assert.assertEquals(linked[counter++], vertex);
        }
    }

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals(4, graph.size());
    }

    @Test
    public void testGetOrderedBeans() throws Exception {
        List<Bean> beans = graph.getOrderedBeans();

        // bean v0 should be the last initialized bean
        Assert.assertEquals(beans.get(3), vertices.get(0).getBean());

        // bean v1 can't be in the first place
        Assert.assertNotEquals(beans.get(0), vertices.get(1).getBean());
    }
}
