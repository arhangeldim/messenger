package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class BeanXmlReader {
    private static final String TAG_ROOT = "root";
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    private String getMandatoryAttribute(Node node, String attribute) throws InvalidConfigurationException {
        Node propertyNode = node.getAttributes().getNamedItem(attribute);
        if (propertyNode == null) {
            throw new InvalidConfigurationException("Missing mandatory attribute '" +
                    attribute + "' of node '" + node.getNodeName() + "'.");
        }

        String value = propertyNode.getNodeValue();
        assert value != null;
        return value;
    }

    private Property parseProperty(Node node) throws InvalidConfigurationException {

        String value;
        ValueType valueType;

        Node valueAttr = node.getAttributes().getNamedItem(ATTR_VALUE);
        Node refAttr = node.getAttributes().getNamedItem(ATTR_REF);

        if ((valueAttr == null && refAttr == null) || (valueAttr != null && refAttr != null)) {
            throw new InvalidConfigurationException("Property must have exactly one of '" +
                    ATTR_VALUE + "' and '" + ATTR_REF + "' attributes.");
        }

        if (valueAttr != null) {
            value = valueAttr.getNodeValue();
            valueType = ValueType.VAL;
        } else {
            value = refAttr.getNodeValue();
            valueType = ValueType.REF;
        }

        String name = getMandatoryAttribute(node, ATTR_NAME);

        assert value != null;
        return new Property(name, value, valueType);
    }

    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = dbFactory.newDocumentBuilder();
            doc = builder.parse(new FileInputStream(pathToFile));
        } catch (SAXException e) {
            throw new InvalidConfigurationException("Invalid XML file: " + e.getMessage());
        } catch (ParserConfigurationException e) {
            // can't do anything
            throw new RuntimeException("Internal error: invalid XML parser configuration.");
        }

        NodeList nodes = doc.getChildNodes();
        if (nodes.getLength() != 1 || !nodes.item(0).getNodeName().equals(TAG_ROOT)) {
            throw new InvalidConfigurationException("Expected single '" + TAG_ROOT + "' node.");
        }

        Node root = nodes.item(0);
        NodeList rootChildren = root.getChildNodes();

        ArrayList<Bean> beans = new ArrayList<>();

        for (int beanIndex = 0; beanIndex < rootChildren.getLength(); beanIndex++) {
            // why isn't it iterable?
            Node beanNode = rootChildren.item(beanIndex);

            if (beanNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (!beanNode.getNodeName().equals(TAG_BEAN)) {
                throw new InvalidConfigurationException("Invalid '" + beanNode.getNodeName() +
                        "' children of '" + TAG_ROOT + "'.");
            }

            NodeList properties = beanNode.getChildNodes();

            String name = getMandatoryAttribute(beanNode, ATTR_BEAN_ID);
            String className = getMandatoryAttribute(beanNode, ATTR_BEAN_CLASS);

            Map<String, Property> beanProperties = new HashMap<>();

            for (int propertyIndex = 0; propertyIndex < properties.getLength(); propertyIndex++) {
                Node propertyNode = properties.item(propertyIndex);

                if (propertyNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                if (!propertyNode.getNodeName().equals(TAG_PROPERTY)) {
                    throw new InvalidConfigurationException("Invalid '" + propertyNode.getNodeName() +
                            "' children of '" + TAG_BEAN + "'.");
                }

                Property property = parseProperty(propertyNode);
                Property old = beanProperties.put(property.getName(), property);
                if (old != null) {
                    throw new InvalidConfigurationException("Duplicate property '" + property.getName() + "'.");
                }
            }

            Bean bean = new Bean(name, className, beanProperties);
            beans.add(bean);
        }

        return beans;
    }

}
