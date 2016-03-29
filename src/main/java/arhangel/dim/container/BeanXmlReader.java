package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

=======
import java.util.List;

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

    public static List<Bean> read(String config) throws InvalidConfigurationException {
        List<Bean> answer = new ArrayList<>();
        Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(config));
        } catch (Exception e) {
            throw new InvalidConfigurationException("Unable to read the xml");
        }
        NodeList root = document.getDocumentElement().getChildNodes();
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < root.getLength(); ++i) {
            Node node = root.item(i);
            if (!TAG_BEAN.equals(node.getNodeName())) {
                continue;
            }
            Bean currentBean = getBeanByNode(node);
            answer.add(currentBean);
            if (ids.contains(currentBean.getName())) {
                throw new InvalidConfigurationException(String
                        .format("Duplicate name %s", currentBean.getName()));
            }
            ids.add(currentBean.getName());
        }
        return answer;
    }

    private static Bean getBeanByNode(Node node) throws InvalidConfigurationException {
        Bean answer = new Bean();
        NamedNodeMap attributes = node.getAttributes();
        answer.setName(attributes.getNamedItem(ATTR_BEAN_ID).getNodeValue());
        answer.setClassName(attributes.getNamedItem(ATTR_BEAN_CLASS).getNodeValue());
        NodeList properties = node.getChildNodes();
        Map<String, Property> answerProperties = new HashMap<>();
        for (int i = 0; i < properties.getLength(); ++i) {
            Node propertyNode = properties.item(i);
            if (!TAG_PROPERTY.equals(propertyNode.getNodeName())) {
                continue;
            }
            Property property = getPropertyByNode(propertyNode);
            if (answerProperties.containsKey(property.getName())) {
                throw new InvalidConfigurationException(String
                        .format("Duplicate property %s in bean %s",
                                property.getName(), answer.getName()));
            }
            answerProperties.put(property.getName(), property);
        }
        answer.setProperties(answerProperties);
        return answer;
    }

    private static Property getPropertyByNode(Node node) throws InvalidConfigurationException {
        Property answer = new Property();
        NamedNodeMap attributes = node.getAttributes();
        answer.setName(attributes.getNamedItem(ATTR_NAME).getNodeValue());
        if (attributes.getNamedItem(ATTR_VALUE) != null) {
            // value
            answer.setType(ValueType.VAL);
            answer.setValue(attributes.getNamedItem(ATTR_VALUE).getNodeValue());
        } else {
            if (attributes.getNamedItem(ATTR_REF) == null) {
                throw new InvalidConfigurationException(String.format("Property %s is neither " +
                        "value nor reference", answer.getName()));
            }
            // reference
            answer.setType(ValueType.REF);
            answer.setValue(attributes.getNamedItem(ATTR_REF).getNodeValue());
        }
        return answer;
    }
}
