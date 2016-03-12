package arhangel.dim.container;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by olegchuikin on 12/03/16.
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
        try {
            File file = new File(pathToFile);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);

            Node root = doc.getFirstChild();

            List<Bean> result = new ArrayList<>();
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeName().equals(TAG_BEAN)) {
                    result.add(parseBean(node));
                }
            }
            return sortBeans(result);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bean parseBean(Node beanNode) {
        Element beanElement = (Element) beanNode;
        String id = beanElement.getAttribute(ATTR_BEAN_ID);
        String clazz = beanElement.getAttribute(ATTR_BEAN_CLASS);
        Map<String, Property> propertyMap = new HashMap<>();
        NodeList childNodes = beanElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(TAG_PROPERTY)) {
                Property property = parseProperty(node);
                propertyMap.put(property.getName(), property);
            }
        }
        return new Bean(id, clazz, propertyMap);
    }

    private Property parseProperty(Node pNode) {
        Element pElement = (Element) pNode;
        String name = pElement.getAttribute(ATTR_NAME);
        if (pElement.hasAttribute(ATTR_REF)) {
            return new Property(name, pElement.getAttribute(ATTR_REF), ValueType.REF);
        } else {
            return new Property(name, pElement.getAttribute(ATTR_VALUE), ValueType.VAL);
        }
    }

    private List<Bean> sortBeans(List<Bean> beans) {
        BeansSorter beansSorter = new BeansSorter(beans);
        return beansSorter.getSorted();
    }

    private class BeansSorter {

        private Map<String, Color> colors;
        private List<Bean> beans;

        public BeansSorter(List<Bean> beans) {
            this.beans = beans;
            colors = new HashMap<>();
            for (Bean bean : beans) {
                colors.put(bean.getName(), Color.WHITE);
            }
        }

        private Color getColor(Bean bean) {
            return colors.get(bean.getName());
        }

        private void setColor(Bean bean, Color color) {
            colors.put(bean.getName(), color);
        }

        private Bean getBeanByName(String name) {
            for (Bean bean : beans)
                if (bean.getName().equals(name))
                    return bean;
            return null;
        }

        public List<Bean> getSorted() {
            List<Bean> result = new ArrayList<>();
            for (Bean bean : beans) {
                if (getColor(bean) == Color.WHITE)
                    innerGetSorted(bean, result);
            }
            Collections.reverse(result);
            return result;
        }

        private void innerGetSorted(Bean bean, List<Bean> forResult) {
            setColor(bean, Color.GREY);
            Collection<Property> values = bean.getProperties().values();
            for (Property property : values) {
                if (property.getType().equals(ValueType.REF)){
                    Bean child = getBeanByName(property.getValue());
                    if (getColor(child).equals(Color.GREY)){
                        throw new LoopInTheBeansException();
                    }
                    if (getColor(child).equals(Color.WHITE)){
                        innerGetSorted(child, forResult);
                    }
                }
            }

            setColor(bean, Color.BLACK);
            forResult.add(bean);
        }
    }

    private enum Color {
        WHITE, GREY, BLACK
    }

    public class LoopInTheBeansException extends RuntimeException{}

}
