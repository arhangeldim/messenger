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


/**
 * Created by Владелец on 15.03.2016.
 */
public class BeanXmlReader {


    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) throws Exception {

        Document doc = getDocument(pathToFile);
        //Node node = doc.getChildNodes().item(0);
        Element root = doc.getDocumentElement();
        //if (node.getNodeType() == Node.ELEMENT_NODE) element = (Element) node;
        NodeList nodeList = root.getElementsByTagName(TAG_BEAN);

        List<Bean> listOfBeans = new ArrayList<Bean>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element;
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) node;
            } else {
                element = null;
            }
            String attrBeanId = element.getAttribute(ATTR_BEAN_ID);
            String attBeanClass = element.getAttribute(ATTR_BEAN_CLASS);
            NodeList nodeList1 = element.getElementsByTagName(TAG_PROPERTY);
            Map<String, Property> hm = new HashMap<String, Property>();
            for (int j = 0; j < nodeList1.getLength(); j++) {
                //Element element1;
                Node node1 = nodeList1.item(j);
                if (node1.getNodeType() == Node.ELEMENT_NODE) {
                    element = (Element) node1;
                }
                String attrName = element.getAttribute(ATTR_NAME);
                ValueType valueType;
                String attr;
                if (element.hasAttribute(ATTR_REF)) {
                    // Property property = new Property(string3, element.getAttribute(ATTR_REF), ValueType.REF);
                    valueType = ValueType.REF;
                    attr = element.getAttribute(ATTR_REF);
                } else {
                    valueType = ValueType.VAL;
                    attr = element.getAttribute(ATTR_VALUE);
                }

                // Property property = new Property(string3, element.getAttribute(ATTR_REF), );

                hm.put(attrName, new Property(attrName, attr, valueType));

            }
            Bean bean = new Bean(attrBeanId, attBeanClass, hm);
            listOfBeans.add(bean);
        }
        return listOfBeans;
    }

    private static Document getDocument(String pathToFile) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new File(pathToFile));
        } catch (Exception exception) {
            String message = "XML parsing error!";
            throw new Exception(message);
        }
    }


}
