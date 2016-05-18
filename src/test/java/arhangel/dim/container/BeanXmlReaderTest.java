package arhangel.dim.container;

import java.util.List;

import org.junit.Test;

import org.junit.Assert;

/**
 *
 */
public class BeanXmlReaderTest {

    @Test
    public void testParseBeans() throws Exception {
        List<Bean> beans = BeanXmlReader.read("config.xml");
        Assert.assertTrue(beans != null);
        Assert.assertTrue(beans.size() > 0);


    }
}
