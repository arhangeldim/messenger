package arhangel.dim.container;



import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
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

/**
 * Created by thefacetakt on 15.03.16.
 */


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

    public List<Bean> parseBeans(String pathToFile)
            throws ParserConfigurationException, IOException, SAXException {
        List<Bean> result = new ArrayList<>();
        File inputFile = new File(pathToFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputFile);
        NodeList nodeList = document.getElementsByTagName(TAG_BEAN);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            NamedNodeMap nodeAttributes = node.getAttributes();
            NodeList properties = ((Element) node)
                    .getElementsByTagName(TAG_PROPERTY);
            Map<String, Property> currentNodeProperties = new HashMap<>();

            for (int j = 0; j < properties.getLength(); ++j) {
                Node propertyNode = properties.item(j);

                NamedNodeMap childNodeAttributes = propertyNode.getAttributes();
                ValueType currentValueType;
                String currentValue;
                if (childNodeAttributes.getNamedItem(ATTR_VALUE) != null) {
                    currentValueType = ValueType.VAL;
                    currentValue = childNodeAttributes.getNamedItem(ATTR_VALUE)
                            .getNodeValue();
                } else {
                    currentValueType = ValueType.REF;
                    currentValue = childNodeAttributes.getNamedItem(ATTR_REF)
                            .getNodeValue();
                }

                currentNodeProperties.put(
                        childNodeAttributes
                                .getNamedItem(ATTR_NAME).getNodeValue(),
                        new Property(
                                childNodeAttributes
                                        .getNamedItem(ATTR_NAME).getNodeValue(),
                                currentValue,
                                currentValueType)
                );
            }
            Bean curBean = new Bean(
                    nodeAttributes.getNamedItem(ATTR_BEAN_ID).getNodeValue(),
                    nodeAttributes.getNamedItem(ATTR_BEAN_CLASS).getNodeValue(),
                    currentNodeProperties);
            result.add(curBean);
        }
        return result;
    }
}
