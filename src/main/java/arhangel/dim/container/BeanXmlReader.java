package arhangel.dim.container;

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

/**
 * Created by vital on 19.03.16.
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

    public List<Bean> parseBeans(String pathToFile) {
        List<Bean> beans = null;
        try {
            File file = new File(pathToFile);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            if (doc.hasChildNodes()) {
                beans = getBeans(doc.getChildNodes());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return beans;
    }

    private List<Bean> getBeans(NodeList nodeList) {
        List<Bean> beans = new ArrayList<>();

        Node rootNode = nodeList.item(0);
        if (rootNode.getNodeType() == Node.ELEMENT_NODE && rootNode.getNodeName() == TAG_ROOT) {
            if (rootNode.hasChildNodes()) {
                NodeList beanNodes = rootNode.getChildNodes();
                for (int countBean = 0; countBean < beanNodes.getLength(); countBean++) {
                    Node beanNode = beanNodes.item(countBean);
                    if (beanNode.getNodeType() == Node.ELEMENT_NODE &&
                            beanNode.getNodeName() == TAG_BEAN) {

                        String beanName = null;
                        String beanClassName = null;
                        Map<String, Property> beanProperties = new HashMap<>();
                        if (beanNode.hasAttributes()) {
                            NamedNodeMap beanNodeMap = beanNode.getAttributes();
                            for (int i = 0; i < beanNodeMap.getLength(); i++) {

                                Node node = beanNodeMap.item(i);
                                switch (node.getNodeName()) {
                                    case ATTR_BEAN_ID:
                                        beanName = new String(node.getNodeValue());
                                        break;
                                    case ATTR_BEAN_CLASS:
                                        beanClassName = new String(node.getNodeValue());
                                        break;
                                }
                            }
                        }

                        if (beanNode.hasChildNodes()) {
                            NodeList propNodes = beanNode.getChildNodes();
                            for (int countProp = 0; countProp < propNodes.getLength(); countProp++) {
                                Node propNode = propNodes.item(countProp);
                                if (propNode.getNodeType() == Node.ELEMENT_NODE &&
                                        propNode.getNodeName() == TAG_PROPERTY) {

                                    Property beanProperty = null;
                                    String propName = null;
                                    String propValue = null;
                                    ValueType propValueType = null;
                                    if (propNode.hasAttributes()) {
                                        NamedNodeMap propNodeMap = propNode.getAttributes();
                                        for (int i = 0; i < propNodeMap.getLength(); i++) {
                                            Node node = propNodeMap.item(i);

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
                                            }
                                        }
                                        beanProperty = new Property(propName, propValue, propValueType);
                                    }
                                    beanProperties.put(propName, beanProperty);
                                }
                            }
                        }
                    beans.add(new Bean(beanName, beanClassName, beanProperties));
                    }
                }
            }
            System.out.println("Node Name =" + rootNode.getNodeName() + " [CLOSE]");

        }

        return beans;
    }
}
