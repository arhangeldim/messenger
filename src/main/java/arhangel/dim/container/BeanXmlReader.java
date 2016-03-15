package arhangel.dim.container;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created by andy on 14.03.2016.
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
        Document doc = this.getDocumentObject(pathToFile);
        NodeList beanNodeList = doc.getElementsByTagName(TAG_BEAN);
        List<Bean> beansList = new LinkedList<>();
        for (int i = 0; i < beanNodeList.getLength(); i++) {
            Element currentBean = (Element) beanNodeList.item(i);
            Map<String, Property> beanAttributes = this.getAttributeMapForBean(currentBean);
            String beanName = currentBean.getAttribute(ATTR_BEAN_ID);
            String beanClass = currentBean.getAttribute(ATTR_BEAN_CLASS);
            beansList.add(
                    new Bean(
                            beanName,
                            beanClass,
                            beanAttributes
                    )
            );
        }
        return beansList;
    }

    private Document getDocumentObject(String pathToFile) throws Exception {
        File xmlFile = new File(pathToFile);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
        Document doc = dbuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Map<String, Property> getAttributeMapForBean(Element element) {
        Map<String, Property> beanAttributes = new HashMap<>();
        NodeList beanAttributeList = element.getElementsByTagName(TAG_PROPERTY);
        for (int j = 0; j < beanAttributeList.getLength(); j++) {
            Element attribute = (Element) beanAttributeList.item(j);
            Property property = null;
            String name = attribute.getAttribute(ATTR_NAME);
            if (attribute.hasAttribute(ATTR_REF)) {
                property = new Property(name,attribute.getAttribute(ATTR_REF),ValueType.REF);
            } else if (attribute.hasAttribute(ATTR_VALUE)) {
                property = new Property(name,attribute.getAttribute(ATTR_VALUE),ValueType.VAL);
            }
            beanAttributes.put(name,property);
        }
        return beanAttributes;
    }
}