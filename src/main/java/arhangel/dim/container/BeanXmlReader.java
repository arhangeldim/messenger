package arhangel.dim.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

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

    public List<Bean> parseBeans(String pathToFile){
        List<Bean> beans = new ArrayList<Bean>();
        try {
            File inputFile = new File(pathToFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(TAG_BEAN);
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                Element eElement = (Element) nNode;
                String beanId = eElement.getAttribute(ATTR_BEAN_ID);
                String beanClass = eElement.getAttribute(ATTR_BEAN_CLASS);
                NodeList pList = doc.getElementsByTagName(TAG_PROPERTY);
                Map<String, Property> properties = new HashMap<String, Property>();
                for (int temp2 = 0; temp2 < pList.getLength(); temp2++) {
                    Node pNode = pList.item(temp2);
                    Element pElement = (Element) pNode;
                    HashMap pAttributes = (HashMap) pElement.getAttributes();
                    String name = (String) pAttributes.get(ATTR_NAME);
                    String value;
                    ValueType type = ValueType.REF;
                    if (pAttributes.containsKey(ATTR_VALUE)){
                        value = (String) pAttributes.get(ATTR_VALUE);
                        type = ValueType.VAL;
                    }
                    else value = (String) pAttributes.get(ATTR_REF);
                    Property property = new Property(name,value,type);
                    properties.put(name,property);
                }
                Bean bean = new Bean(beanId,beanClass,properties);
                beans.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();}

        return beans;
    }

}
