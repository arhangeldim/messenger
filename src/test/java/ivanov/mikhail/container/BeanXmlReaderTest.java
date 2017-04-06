package ivanov.mikhail.container;

import java.util.List;

import org.junit.Test;

import org.junit.Assert;

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
