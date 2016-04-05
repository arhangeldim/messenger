package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    public List<Bean> parseBeans(String pathToFile) {
        List<Bean> beans = new ArrayList<Bean>();
        try {
            File inputFile = new File(pathToFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList ndList = doc.getElementsByTagName(TAG_BEAN);
            for (int temp = 0; temp < ndList.getLength(); temp++) {
                Node ndNode = ndList.item(temp);
                Element elElement = (Element) ndNode;
                String beanId = elElement.getAttribute(ATTR_BEAN_ID);
                String beanClass = elElement.getAttribute(ATTR_BEAN_CLASS);
                NodeList prList = elElement.getElementsByTagName(TAG_PROPERTY);
                Map<String, Property> properties = new HashMap<String, Property>();
                for (int temp2 = 0; temp2 < prList.getLength(); temp2++) {
                    Node prNode = prList.item(temp2);
                    Element prElement = (Element) prNode;
                    String name = prElement.getAttribute(ATTR_NAME);
                    String value;
                    ValueType type = ValueType.REF;
                    if (prElement.getAttribute(ATTR_VALUE) != "") {
                        value = prElement.getAttribute(ATTR_VALUE);
                        type = ValueType.VAL;
                    } else {
                        value = prElement.getAttribute(ATTR_REF);
                    }
                    Property property = new Property(name,value,type);
                    properties.put(name,property);
                }
                Bean bean = new Bean(beanId,beanClass,properties);
                beans.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beans;
    }

}
