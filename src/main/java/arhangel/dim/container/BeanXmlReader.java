package arhangel.dim.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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

    public List<Bean> parseBeans(String pathToFile) throws InvalidConfigurationException {
        System.out.print("BeanXML Parses file" + pathToFile + "\n");
        List<Bean> ls = new ArrayList<>();
        File file = new File(pathToFile);
        if (file.exists()) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList beanList = doc.getElementsByTagName(TAG_BEAN);
                for (int beanIndex = 0; beanIndex < beanList.getLength(); beanIndex++) {
                    Node beanNode = beanList.item(beanIndex);
                    //System.out.print("*****\nnext Bean:\n");
                    Element beanEl = (Element) beanNode;
                    String className = beanEl.getAttribute(ATTR_BEAN_CLASS);
                    //System.out.print("Class: " + className + "\n");
                    String id = beanEl.getAttribute(ATTR_BEAN_ID);
                    //System.out.print("Id: " + id + "\n");
                    Map<String, Property> properties = new HashMap<>();
                    NodeList propList = ((Element) beanNode).getElementsByTagName(TAG_PROPERTY);
                    for (int i = 0; i < propList.getLength(); i++) {
                        Element propEl = (Element) (propList.item(i));
                        String name = propEl.getAttribute(ATTR_NAME);
                        String ref = propEl.getAttribute(ATTR_REF);
                        String val = propEl.getAttribute(ATTR_VALUE);
                        /*System.out.print("Property:\n");
                        System.out.print("Name: " + name + "\n");
                        System.out.print("Val: " + val + "\n");
                        System.out.print("Ref: " + ref + "\n");*/
                        if (ref != "" && val == "") {
                            properties.put(name, new Property(name, ref, ValueType.REF));
                        } else if (ref == "" && val != "") {
                            properties.put(name, new Property(name, val, ValueType.VAL));
                        }
                    }
                    ls.add(new Bean(id, className, properties));
                }
            } catch (ParserConfigurationException e) {
                throw new InvalidConfigurationException("Problem with reading conf file");
            } finally {
                return ls;
            }
        }
        return ls;
    }

}
