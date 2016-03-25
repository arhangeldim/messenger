package arhangel.dim.container;


//import org.w3c.dom.*;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanXmlReader {
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException {

        List<Bean> returnList = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            File inputFile = new File(pathToFile);
            Document document = docBuilder.parse(inputFile);
            document.getDocumentElement().normalize();
            NodeList beanList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < beanList.getLength(); ++i) {
                Node beanNode = beanList.item(i);
                if (!beanNode.getNodeName().equals(TAG_BEAN)) { // not a bean
                    continue;
                }
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = beanNode.getAttributes();
                    String beanName = attributes.getNamedItem(ATTR_BEAN_ID).getNodeValue();
                    String beanClass = attributes.getNamedItem(ATTR_BEAN_CLASS).getNodeValue();
                    Map<String, Property> beanProperties = parseBeanProperties(beanNode);

                    Bean bean = new Bean(beanName, beanClass, beanProperties);
                    returnList.add(bean);
                } else {
                    throw new Exception("Bean is not ELEMENT_NODE");
                }
            }
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
        return returnList;
    }

    private Map<String, Property> parseBeanProperties(Node beanNode) throws Exception {
        Map<String, Property> propertyMap = new HashMap<>();

        if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
            NodeList propertyList = beanNode.getChildNodes();
            for (int i = 0; i < propertyList.getLength(); ++i) {
                Node propertyNode = propertyList.item(i);
                if (!propertyNode.getNodeName().equals(TAG_PROPERTY)) { // not a property
                    continue;
                }
                if (propertyNode.hasChildNodes()) {
                    throw new Exception("Property has children");
                }
                if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = propertyNode.getAttributes();
                    String propertyName = attributes.getNamedItem(ATTR_NAME).getNodeValue();
                    ValueType propertyValueType = ValueType.REF;

                    Node propertyValueNode = attributes.getNamedItem(ATTR_REF);

                    if (propertyValueNode == null) {
                        propertyValueNode = attributes.getNamedItem(ATTR_VALUE);
                        propertyValueType = ValueType.VAL;
                    }

                    if (propertyValueNode == null) {
                        throw new Exception("No value in property tag");
                    }

                    String propertyValue = propertyValueNode.getNodeValue();

                    Property property = new Property(propertyName, propertyValue, propertyValueType);

                    propertyMap.put(propertyName, property);
                } else {
                    throw new Exception("Property is not ELEMENT_NODE");
                }

            }
        } else {
            throw new Exception("Bean is not ELEMENT_NODE");
        }
        return propertyMap;
    }

}
