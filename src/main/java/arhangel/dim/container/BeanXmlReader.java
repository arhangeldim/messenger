package arhangel.dim.container;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valeriyasin on 3/15/16.
=======
import java.util.List;

/**
 *
>>>>>>> 5044e64aedcc627f70c5d919734be1e8583b899e
 */
public class BeanXmlReader {
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";
    private static final String TAG_GEAR = "gear";
    private static final String TAG_ENGINE = "engine";


    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException {
        List<Bean> graphWithoutEdges = new LinkedList<>();

        try {
            File inputFile = new File(pathToFile);
            //System.out.println(pathToFile);
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            try {
                Document doc = documentBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
                NodeList beans = doc.getElementsByTagName(TAG_BEAN);


                for (int i = 0; i < beans.getLength(); ++i) {
                    Node bean = beans.item(i);
                    NamedNodeMap attributes = bean.getAttributes();
                    Node idAttrib = attributes.getNamedItem(ATTR_BEAN_ID);
                    // System.out.printf(idAttrib.toString());
                    Node classAttrib = attributes.getNamedItem(ATTR_BEAN_CLASS);
                    //System.out.printf(classAttrib.toString());
                    NodeList properties = ((Element) bean).getElementsByTagName(TAG_PROPERTY);
                    HashMap<String, Property> propertyHashMap = new HashMap<>();
                    //System.out.println(properties.getLength());

                    for (int j = 0; j < properties.getLength(); j++) {
                        Node property = properties.item(j);
                        NamedNodeMap propertyAttributes = property.getAttributes();
                        Node nameAttrib = propertyAttributes.getNamedItem(ATTR_NAME);
                        String propertyType = nameAttrib.getNodeValue();
                        Property newProp;
                        if (propertyAttributes.getNamedItem(ATTR_REF) != null) {
                            //System.out.println(propertyType);
                            Node refAttrib = propertyAttributes.getNamedItem(ATTR_REF);
                            String refAttrString = refAttrib.getNodeValue();
                            //System.out.println("REFEERENCE" + refAttrString);
                            newProp = new Property(propertyType, refAttrString, ValueType.REF);
                        } else {
                            //System.out.println(propertyType);
                            Node valAttrib = propertyAttributes.getNamedItem(ATTR_VALUE);
                            String valAttrString = valAttrib.getNodeValue();
                            //System.out.println(valAttrString);
                            newProp = new Property(propertyType, valAttrString, ValueType.VAL);
                        }
                        propertyHashMap.put(nameAttrib.getNodeValue(), newProp);
                    }
                    Bean newBean = new Bean(idAttrib.getNodeValue(), classAttrib.getNodeValue(), propertyHashMap);
                    //BeanVertex newBeanVertex = new BeanVertex(newBean);
                    graphWithoutEdges.add(newBean);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            throw new InvalidConfigurationException("can't read xml File");
        }
        return graphWithoutEdges;
    }
}
