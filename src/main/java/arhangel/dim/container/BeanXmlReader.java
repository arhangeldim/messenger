package arhangel.dim.container;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 *
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
        List<Bean> beans = new ArrayList<>();

        try {
            File inputFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName(TAG_BEAN);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element beanElement = (Element) nodeList.item(i);
                NodeList propertiesNodes = beanElement.getElementsByTagName(TAG_PROPERTY);

                String beanId = beanElement.getAttribute(ATTR_BEAN_ID);
                String beanClass = beanElement.getAttribute(ATTR_BEAN_CLASS);
                Map<String, Property> beanProps = new HashMap<>();

                for (int j = 0; j < propertiesNodes.getLength(); j++) {
                    Property beanProperty = null;
                    String propName = null;
                    String propValue = null;
                    ValueType propValueType = null;

                    Element propElement = (Element) propertiesNodes.item(j);
                    NamedNodeMap propAttr = propElement.getAttributes();

                    for (int k = 0; k < propAttr.getLength(); k++) {
                        Node node = propAttr.item(k);
                        switch (node.getNodeName()) {
                            case ATTR_NAME:
                                propName = new String(node.getNodeValue());
                                break;
                            case ATTR_REF:
                                propValue = new String(node.getNodeValue());
                                propValueType = ValueType.REF;
                                break;
                            case ATTR_VALUE:
                                propValue = new String(node.getNodeValue());
                                propValueType = ValueType.VAL;
                                break;
                            default:
                                break;
                        }
                        beanProperty = new Property(propName, propValue, propValueType);
                    }
                    beanProps.put(propName, beanProperty);
                }
                beans.add(new Bean(beanId, beanClass, beanProps));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return beans;
    }

}

