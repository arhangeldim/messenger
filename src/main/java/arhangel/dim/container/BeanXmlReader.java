package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by spec45as on 3/22/2016.
 */
public class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public BeanXmlReader() {
    }

    public Document loadXmlFile(String name) throws IOException, ParserConfigurationException, SAXException {
        return loadXmlFile(new FileInputStream(name));
    }

    public Document loadXmlFile(InputStream is) throws IOException, ParserConfigurationException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
    }

    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException {
        Document document = null;
        List<Bean> allBeans = new ArrayList<Bean>();
        try {
            document = loadXmlFile(pathToFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidConfigurationException(String.format("XML file not found: %s", pathToFile));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new InvalidConfigurationException(String.format("XML parser configuration error on: %s", pathToFile));
        } catch (SAXException e) {
            e.printStackTrace();
            throw new InvalidConfigurationException(String.format("XML file SAX parsing error: %s", pathToFile));
        }

        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName(TAG_BEAN);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element beanElement = (Element) node;
                String beanId = beanElement.getAttribute(ATTR_BEAN_ID);
                String beanClass = beanElement.getAttribute(ATTR_BEAN_CLASS);
                NodeList allProperties = beanElement.getElementsByTagName(TAG_PROPERTY);

                Map<String, Property> propertiesMap = new HashMap<String, Property>();
                for (int j = 0; j < allProperties.getLength(); j++) {
                    Element currentProperty = (Element) allProperties.item(j);
                    String propertyAtrName = currentProperty.getAttribute(ATTR_NAME);
                    String propertyAtrValue = currentProperty.getAttribute(ATTR_VALUE);

                    ValueType propertyValueType = ValueType.VAL;
                    if (propertyAtrValue.equals("")) {
                        propertyValueType = ValueType.REF;
                        propertyAtrValue = currentProperty.getAttribute(ATTR_REF);
                    }

                    Property newProperty = new Property(propertyAtrName, propertyAtrValue, propertyValueType);
                    propertiesMap.put(propertyAtrName, newProperty);

                }
                Bean newBean = new Bean(beanId, beanClass, propertiesMap);
                allBeans.add(newBean);
            }


        }

        return allBeans;
    }
}