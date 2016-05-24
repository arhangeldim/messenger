package arhangel.dim.container;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;






public class BeanXmlReader {

    private static final String tagBean = "bean";
    private static final String tagProperty = "property";
    private static final String attrName = "name";
    private static final String attrValue = "val";
    private static final String attrRef = "ref";
    private static final String attrBeanId = "id";
    private static final String attrBeanClass = "class";

    public List<Bean> parseBeans(String pathToFile) {

        List<Bean> beans = new ArrayList<>();

        String beanId;
        String className;
        String propName;
        String propVal;
        ValueType type;

        try {
            File xmlFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xmlFile);

            NodeList nlist = doc.getElementsByTagName("bean");

            for (int i = 0; i < nlist.getLength(); i++) {

                Node curNode = nlist.item(i);

                Element element = (Element) curNode;

                beanId = element.getAttribute(attrBeanId);
                className = element.getAttribute(attrBeanClass);

                NodeList props = element.getElementsByTagName(tagProperty);
                Map properties = new HashMap();

                for (int propCount = 0; propCount < props.getLength(); propCount++) {

                    propName = props.item(propCount).getAttributes().item(0).getNodeValue();

                    if (props.item(propCount).getAttributes().item(1).getNodeName().equals(attrRef)) {
                        type = ValueType.REF;
                    } else if (props.item(propCount).getAttributes().item(1).getNodeName().equals(attrValue)) {
                        type = ValueType.VAL;
                    } else {
                        type = null;
                    }

                    propVal = props.item(propCount).getAttributes().item(1).getNodeValue();

                    properties.put(propName, new Property(propName, propVal, type));
                }

                beans.add(new Bean(beanId, className, properties));
            }
        } catch (Exception exc) {
            exc.getCause();
        }

        return beans;
    }

}