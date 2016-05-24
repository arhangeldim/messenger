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
        Context reader = new Context("config.xml");
        List<Bean> beans = reader.getBeans();
        Assert.assertTrue(beans != null);
        Assert.assertTrue(beans.size() > 0);


    }
}
