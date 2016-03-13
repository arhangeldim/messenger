package arhangel.dim.container;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miron on 12.03.16.
 */
public class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) {

        SAXBuilder saxBuilder = new SAXBuilder();
        File inputFile = new File(pathToFile);
        List<Bean> result = new ArrayList<>();
        try {
            saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(inputFile);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.getChildren(TAG_BEAN);
            String name;
            String className;
            Map<String, Property> properties;
            for (Element el:
                     elements) {
                name = el.getAttributeValue(ATTR_BEAN_ID);
                className = el.getAttributeValue(ATTR_BEAN_CLASS);
                properties = new HashMap<>();
                for (Element p :
                        el.getChildren(TAG_PROPERTY)) {
                    String propertieName = p.getAttributeValue(ATTR_NAME);
                    String propertieValue = (p.getAttributeValue(ATTR_REF) != null ? p.getAttributeValue(ATTR_REF) :
                            p.getAttributeValue(ATTR_VALUE));
                    ValueType propertieType = (p.getAttributeValue(ATTR_REF) != null ? ValueType.VAL : ValueType.REF);
                    properties.put(propertieName, new Property(propertieName, propertieValue, propertieType));
                }
                result.add(new Bean(name, className, properties));
            }
        } catch (JDOMException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return result;
    }
}
