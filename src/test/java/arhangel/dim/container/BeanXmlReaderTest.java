package arhangel.dim.container;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.junit.Assert;

/**
 *
 */
public class BeanXmlReaderTest {

    @Test
    public void testParseBeans() throws Exception {
        BeanXmlReader reader = new BeanXmlReader();
        Map<String, Bean> beans = reader.parseBeans("config.xml");
        Assert.assertTrue(beans != null);
        Assert.assertTrue(beans.size() > 0);


    }
}
