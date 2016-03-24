package arhangel.dim.container;

/**
 * Created by nexx0f on 15.03.16.
 */

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
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

    public List<Bean> parseBeans(String pathToFile) throws IOException, SAXException, ParserConfigurationException {
        File fXmlFile = new File("./example.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();
        List<Bean> returningList;
        if (doc.hasChildNodes()) {
            NodeList beanNodes = doc.getChildNodes();

            for (int count = 0; count < beanNodes.getLength(); count++) {
                Node tempNode = beanNodes.item(count);
                Bean newBean;
                String beanId;
                String beanClass;
                if (tempNode.hasAttributes()) {
                    NamedNodeMap nodeMap = tempNode.getAttributes();

                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        //System.out.println("attr name : " + node.getNodeName());
                        //System.out.println("attr value : " + node.getNodeValue());
                        if (node.getNodeName().compareTo(ATTR_BEAN_ID) == 0)
                            beanId = node.getNodeValue();
                        else if (node.getNodeName().compareTo(ATTR_BEAN_CLASS) == 0)
                            beanClass = node.getNodeValue();
                    }
                }

                //So, now it's about properties

                NodeList properties = tempNode.getChildNodes();
                Map<String, Property> propMap = new HashMap<>();
                for (int i = 0; i < properties.getLength(); i++) {
                    Node prop = properties.item(i);

                    String name = "";
                    String val = "";
                    ValueType type = ValueType.REF;

                    if (prop.getNodeName().compareTo(TAG_PROPERTY) == 0) {
                        if (prop.hasAttributes()) {
                            NamedNodeMap nodeMap = tempNode.getAttributes();

                            for (int i = 0; i < nodeMap.getLength(); i++) {
                                Node node = nodeMap.item(i);
                                //System.out.println("attr name : " + node.getNodeName());
                                //System.out.println("attr value : " + node.getNodeValue());
                                if (node.getNodeName().compareTo(ATTR_NAME) == 0)
                                    name = node.getNodeValue();
                                else if (node.getNodeName().compareTo(ATTR_REF) == 0) {
                                    val = node.getNodeValue();
                                    type = ValueType.REF;
                                } else if (node.getNodeName().compareTo(ATTR_VALUE) == 0) {
                                    val = node.getNodeValue();
                                    type = ValueType.VAL;
                                }
                            }
                        }
                    }

                    propMap.put(name, new Property(name, val, type));
                }
                returningList.add(new Bean(beanId, beanClass, propMap));
            }
        }
        return returningList;
    }
}
