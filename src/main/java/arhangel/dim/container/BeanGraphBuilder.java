package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thefacetakt on 15.03.16.
 */
public class BeanGraphBuilder {
    public BeanGraph buildFromXml(String pathToFile) throws IOException,
            SAXException, ParserConfigurationException {
        BeanGraph result = new BeanGraph();
        Map<String, BeanVertex> vertices = new HashMap<>();

        List<Bean> beans = new BeanXmlReader().parseBeans(pathToFile);

        beans.forEach(bean ->
                vertices.put(bean.getName(), result.addVertex(bean)));

        beans.forEach(bean -> bean.getProperties().forEach(
                (name, property) -> {
                    if (property.getType() == ValueType.REF) {
                        result.addEdge(vertices.get(bean.getName()),
                                vertices.get(property.getValue()));
                    }
                })
        );
        return result;
    }
}
