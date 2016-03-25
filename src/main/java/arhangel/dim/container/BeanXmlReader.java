package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;



/**
 * Представляет тег bean из конфига
 */
class BeanXmlReader {

//    public static void main(String[] args) {
//        try {
//            System.out.println();
//            BeanXmlReader beanReader = new BeanXmlReader();
//            BeanGraph beanGraph = new BeanGraph();
//            System.out.println(beanGraph.sorted(beanReader.parseBeans("config.xml")));
//
//        } catch (InvalidConfigurationException e) {
//            System.err.println(e.getMessage());
//        }
//    }

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) {

        try {

            List<Bean> beans = new ArrayList<>();

            File xmlFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName(TAG_BEAN);

            for (int currentBeam = 0; currentBeam < list.getLength(); currentBeam++) {

                Node node = list.item(currentBeam);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    String name;
                    String className;
                    Map<String, Property> properties = new HashMap<>();

                    name = element.getAttribute(ATTR_BEAN_ID);
                    className = element.getAttribute(ATTR_BEAN_CLASS);
                    NodeList propertyNodes = element.getElementsByTagName(TAG_PROPERTY);
                    for (int currentProperty = 0; currentProperty < propertyNodes.getLength(); currentProperty++) {
                        if (propertyNodes.item(currentProperty).getNodeType() == Node.ELEMENT_NODE) {
                            Element currentElement = (Element) propertyNodes.item(currentProperty);
                            if (!currentElement.getAttribute(ATTR_REF).matches("")) {
                                Property property = new Property(currentElement.getAttribute(ATTR_NAME),
                                        currentElement.getAttribute(ATTR_REF), ValueType.REF);
                                properties.put(currentElement.getAttribute(ATTR_NAME), property);
                            } else {
                                Property property = new Property(currentElement.getAttribute(ATTR_NAME),
                                        currentElement.getAttribute(ATTR_VALUE), ValueType.VAL);
                                properties.put(currentElement.getAttribute(ATTR_NAME), property);
                            }
                        }
                    }


                    Bean bean = new Bean(name, className, properties);
                    beans.add(bean);
                }

            }

            return  beans;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
