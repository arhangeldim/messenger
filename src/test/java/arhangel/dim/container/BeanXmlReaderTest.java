package arhangel.dim.container;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class BeanXmlReaderTest {

    @Test
    public void testParseBeans() throws Exception {
        BeanXmlReader reader = new BeanXmlReader();
        List<Bean> beans = reader.parseBeans("config.xml");
        Assert.assertTrue(beans != null);
        Assert.assertTrue(beans.size() > 0);


    }
}
