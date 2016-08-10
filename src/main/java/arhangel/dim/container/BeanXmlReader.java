package arhangel.dim.container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeanXmlReader {

    private static Logger log = LoggerFactory.getLogger(BeanXmlReader.class);

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    List<Bean> beans = new ArrayList<>();

    public List<Bean> parseBeans(String pathToFile) throws Exception {
        Document config = readXml(pathToFile);
        Element root = config.getDocumentElement();
        NodeList nodes = root.getChildNodes();

        //проверка на уникальность id
        Set<String> uniqueId = new HashSet<>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (TAG_BEAN.equals(node.getNodeName())) {
                parseBean(node);
                if (uniqueId.contains(beans.get(beans.size() - 1).getName())) {
                    throw new InvalidConfigurationException("Object name is not unique");
                }
                uniqueId.add(beans.get(beans.size() - 1).getName());
            }
        }

        return beans;
    }

    private void parseBean(Node bean) throws Exception {
        NamedNodeMap attr = bean.getAttributes();
        Node name = attr.getNamedItem(ATTR_BEAN_ID);
        String nameVal = name.getNodeValue();
        String classVal = attr.getNamedItem(ATTR_BEAN_CLASS).getNodeValue();
        //log.info("BEAN: [name: {}, class: {}]", nameVal, classVal);

        // ищем все проперти внутри
        NodeList list = bean.getChildNodes();
        Map<String, Property> properties = new HashMap<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (TAG_PROPERTY.equals(node.getNodeName())) {
                Property property = parseProperty(node);
                //log.info("\tSET {}", property);
                properties.put(property.getName(), property);
            }
        }
        //
        beans.add(new Bean(nameVal, classVal, properties));
    }

    private Property parseProperty(Node node) throws Exception {
        NamedNodeMap map = node.getAttributes();
        String name = map.getNamedItem(ATTR_NAME).getNodeValue();
        Node val = map.getNamedItem(ATTR_VALUE);
        if (val != null) {
            // если значение примитивного типа
            return new Property(name, val.getNodeValue(), ValueType.VAL);
        } else {
            // если значение ссылочного типа
            val = map.getNamedItem(ATTR_REF);
            if (val != null) {
                return new Property(name, val.getNodeValue(), ValueType.REF);
            } else {
                throw new InvalidConfigurationException("Failed to parse property " + name);
            }
        }
    }

    private Document readXml(String path) throws Exception {
        File file = new File(path);
        //log.info("Context configuration xml: " + file.getAbsolutePath());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    public List<Bean> getBeans() {
        return beans;
    }

}
