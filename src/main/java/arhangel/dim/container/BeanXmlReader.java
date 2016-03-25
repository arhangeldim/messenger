package arhangel.dim.container;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import java.util.HashMap;

public class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String fileName) throws ParserConfigurationException {
        try {
            File fxml = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(fxml);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName(TAG_BEAN);
            ArrayList<Bean> arrayBean = new ArrayList<Bean>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element beanElement = (Element) node;
                    NodeList allProperties = beanElement.getElementsByTagName(TAG_PROPERTY);

                    Map<String, Property> mapProperty = new HashMap<String, Property>();
                    for (int j = 0; j < allProperties.getLength(); j++) {
                        Element currentProperty = (Element) allProperties.item(j);
                        String nameProperty = currentProperty.getAttribute(ATTR_NAME);
                        String valueProperty = new String();
                        if (currentProperty.getAttribute(ATTR_VALUE).equals("")) {
                            valueProperty = currentProperty.getAttribute(ATTR_REF);
                            Property property = new Property(nameProperty, valueProperty, ValueType.REF);
                            mapProperty.put(nameProperty, property);
                        } else {
                            valueProperty = currentProperty.getAttribute(ATTR_VALUE);
                            Property property = new Property(nameProperty, valueProperty, ValueType.VAL);
                            mapProperty.put(nameProperty, property);
                        }
                    }
                    Bean bean = new Bean(beanElement.getAttribute(ATTR_BEAN_ID),
                            beanElement.getAttribute(ATTR_BEAN_CLASS), mapProperty);
                    arrayBean.add(bean);
                }
            }
            return arrayBean;

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new ParserConfigurationException(ex.getMessage());
        }
    }
}