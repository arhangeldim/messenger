package arhangel.dim.container;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tatiana on 12.03.16.
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
        try {
            File xmlFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlFile);

            NodeList beanNodeList = document.getElementsByTagName(TAG_BEAN);
            List<Bean> beanList = new ArrayList<Bean>();
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                Node beanNode = beanNodeList.item(i);

                if (beanNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element beanElement = (Element) beanNode;

                String id = beanElement.getAttribute(ATTR_BEAN_ID);
                String clazz = beanElement.getAttribute(ATTR_BEAN_CLASS);

                NodeList propertyList = beanElement.getElementsByTagName(TAG_PROPERTY);
                HashMap<String, Property> properties = new HashMap<String, Property>();
                for (int j = 0; j < propertyList.getLength(); j++) {
                    Node propertyNode = propertyList.item(j);

                    if (propertyNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    Element propertyElement = (Element) propertyNode;

                    String name = propertyElement.getAttribute(ATTR_NAME);
                    String ref_value = propertyElement.getAttribute(ATTR_REF);

                    ValueType ref;
                    String val;
                    if (ref_value != null) {
                        ref = ValueType.REF;
                        val = ref_value;
                    } else {
                        ref = ValueType.VAL;
                        val = propertyElement.getAttribute(ATTR_VALUE);
                    }

                    Property property = new Property(name, val, ref);
                    properties.put(name, property);
                }
                Bean bean = new Bean(id.substring(0, id.length() - 4), clazz, properties);
                beanList.add(bean);
            }

            return beanList;

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return null;
        }
    }
}
