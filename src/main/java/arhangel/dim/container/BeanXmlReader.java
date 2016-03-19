package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    private Map<String, Property> parseProperties(NodeList beanChildren) {
        Map<String, Property> propertyMap = new HashMap<>();
        for (int i = 0; i < beanChildren.getLength(); i++) {
            if ( ! (beanChildren.item(i) instanceof Element)) {
                continue;
            }
            String name = beanChildren.item(i).getAttributes().getNamedItem(ATTR_NAME).getNodeValue();
            Node value = beanChildren.item(i).getAttributes().getNamedItem(ATTR_VALUE);
            Node ref = beanChildren.item(i).getAttributes().getNamedItem(ATTR_REF);
            Property newProperty;
            if (value == null) {
                newProperty = new Property(name, ref.getNodeValue(), ValueType.REF);
            } else {
                newProperty = new Property(name, value.getNodeValue(), ValueType.VAL);
            }
            propertyMap.put(name, newProperty);
        }
        return propertyMap;
    }

    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException {
        List<Bean> result = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(false);
        DocumentBuilder builder;
        Document doc;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
            doc = builder.parse(new File(pathToFile));
        } catch (SAXException e) {
            throw new InvalidConfigurationException("SAXException");
        } catch (ParserConfigurationException e) {
            throw new InvalidConfigurationException("ParserConfigurationException");
        } catch (IOException e) {
            throw new InvalidConfigurationException("IOException");
        }
        NodeList beanList = doc.getElementsByTagName(TAG_BEAN);
        for (int i = 0; i < beanList.getLength(); i++) {
            String name = beanList.item(i).getAttributes().getNamedItem(ATTR_BEAN_ID).getNodeValue();
            String className = beanList.item(i).getAttributes().getNamedItem(ATTR_BEAN_CLASS).getNodeValue();
            NodeList beanChildren = beanList.item(i).getChildNodes();
            result.add(new Bean(name, className, parseProperties(beanChildren)));
        }
        return result;
    }
}