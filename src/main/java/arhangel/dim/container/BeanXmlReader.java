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

public class BeanXmlReader {

    final String TAG_BEAN = "bean";
    final String TAG_PROPERTY = "property";
    final String ATTR_NAME = "name";
    final String ATTR_VALUE = "val";
    final String ATTR_REF = "ref";
    final String ATTR_BEAN_ID = "id";
    final String ATTR_BEAN_CLASS = "class";

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

                beanId = element.getAttribute(ATTR_BEAN_ID);
                className = element.getAttribute(ATTR_BEAN_CLASS);

                NodeList props = element.getElementsByTagName(TAG_PROPERTY);
                Map properties = new HashMap();

                for (int propCount = 0; propCount < props.getLength(); propCount++) {

                    propName = props.item(propCount).getAttributes().item(0).getNodeValue();

                    if (props.item(propCount).getAttributes().item(1).getNodeName().equals(ATTR_REF)) {
                        type = ValueType.REF;
                    } else if (props.item(propCount).getAttributes().item(1).getNodeName().equals(ATTR_VALUE)) {
                        type = ValueType.VAL;
                    } else type = null;

                    propVal = props.item(propCount).getAttributes().item(1).getNodeValue();

                    properties.put(propName, new Property(propName, propVal, type));
                }

                beans.add(new Bean(beanId, className, properties));
            }
        }

        catch (Exception exc) {
            exc.getCause();
        }

        return beans;
    }
}
