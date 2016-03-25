package ivanov.mikhail.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arhangel.dim.container.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.Assert;

/**
 *
 */
public class BeanGraphTest {

    private BeanGraph graph;
    private List<ivanov.mikhail.container.BeanVertex> vertices;

    @Before
    public void initTest() {
        graph = new BeanGraph();
        ivanov.mikhail.container.BeanVertex v0 = graph.addVertex(new Bean("0", null, null));
        ivanov.mikhail.container.BeanVertex v1 = graph.addVertex(new Bean("1", null, null));
        ivanov.mikhail.container.BeanVertex v2 = graph.addVertex(new Bean("2", null, null));
        ivanov.mikhail.container.BeanVertex v3 = graph.addVertex(new Bean("3", null, null));

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
    @Ignore
    public void testGetLinked() throws Exception {
        ivanov.mikhail.container.BeanVertex[] linked = new ivanov.mikhail.container.BeanVertex[]{vertices.get(1), vertices.get(2)};
        int counter = 0;
        for (ivanov.mikhail.container.BeanVertex vertex : graph.getLinked(vertices.get(0))) {
            Assert.assertEquals(linked[counter++], vertex);
        }
    }

    @Test
    @Ignore
    public void testSize() throws Exception {
        Assert.assertEquals(4, graph.size());
    }
}
