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

    Node node;
    Element element;
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) throws Exception {

        // if(node.getNodeType()==Node.ELEMENT_NODE)    element=(Element)node;   как кастовать

        Document doc = getDocument(pathToFile);
        Node node = doc.getChildNodes().item(0);
        if (node.getNodeType() == Node.ELEMENT_NODE) element = (Element) node;
        NodeList nodeList = element.getElementsByTagName(TAG_BEAN);

        List<Bean> var = new ArrayList<Bean>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) element = (Element) node;
            String string1 = element.getAttribute(ATTR_BEAN_ID);
            String string2 = element.getAttribute(ATTR_BEAN_CLASS);
            nodeList = element.getElementsByTagName(TAG_PROPERTY);
            Map<String, Property> hm = new HashMap<String, Property>();
            for (int j = 0; j < nodeList.getLength(); j++) {

                node = nodeList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) element = (Element) node;
                String string3 = element.getAttribute(ATTR_NAME);
                ValueType a;
                if (element.hasAttribute(ATTR_REF)) {
                    // Property property = new Property(string3, element.getAttribute(ATTR_REF), ValueType.REF);
                    a = ValueType.REF;
                    String string4 = element.getAttribute(ATTR_REF);
                } else {
                    a = ValueType.VAL;
                    String string4 = element.getAttribute(ATTR_VALUE);
                }

                // Property property = new Property(string3, element.getAttribute(ATTR_REF), a);

                hm.put(string3, new Property(string3, element.getAttribute(ATTR_REF), a));

            }
            Bean bean = new Bean(string1, string2, hm);
            var.add(bean);
        }
        return var;
    }

    private static Document getDocument(String pathToFile) throws Exception {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(new File(pathToFile));
        } catch (Exception exception) {
            String message = "XML parsing error!";
            throw new Exception(message);
        }
    }


}
