package arhangel.dim.container;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import arhangel.dim.container.beans.Car;

public class Context {

    private static Logger log = LoggerFactory.getLogger(Context.class);

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";

    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";

    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";


    List<Bean> beans = new ArrayList<>();

    Map<String, Object> objectsById = new HashMap<>();
    Map<String, Object> objectsByClass = new HashMap<>();

    public static void main(String[] args) throws Exception {

        // Dynamic config
        Context context = new Context("config.xml");
        Car car = (Car) context.getBeanByName("carBean");
    }

    public Context(String xmlPath) throws InvalidConfigurationException {
        Document config = readXml(xmlPath);
        Element root = config.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (TAG_BEAN.equals(node.getNodeName())) {
                parseBean(node);
            }
        }

        // прочитали xml и знаем все о конфигурации
        try {
            instantiateBeans();
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
    }

    public Object getBeanByName(String beanName) {
        return objectsById.get(beanName);
    }

    public Object getBeanByClass(String className) {
        return objectsByClass.get(className);
    }

    public void instantiateBeans() throws Exception {
        log.info("beans: {}", beans);
        for (Bean bean : beans) {
            // по имени класса его можно инстанцировать
            // обязательно должен быть дефолтный конструктор
            String className = bean.getClassName();
            Class clazz = Class.forName(className);
            // ищем дефолтный конструктор
            Object ob = clazz.newInstance();


            for (String name : bean.getProperties().keySet()) {
                // ищем поле с таким именен внутри класса
                // учитывая приватные
                Field field = clazz.getDeclaredField(name);
                if (field == null) {
                    throw new InvalidConfigurationException("Failed to set field [" + name + "] for class " + clazz.getName());
                }
                Property prop = bean.getProperties().get(name);
                // Чтобы изменять приватные поля
                field.setAccessible(true);

                // храним тип данных
                Type type = field.getType();

                switch (prop.getType()) {
                    case VAL:
                        field.set(ob, convert(type.getTypeName(), prop.getValue()));
                        break;
                    case REF:
                        String refName = prop.getValue();
                        if (objectsById.containsKey(refName)) {
                            field.set(ob, objectsById.get(refName));
                        } else {
                            throw new InvalidConfigurationException("Failed to instantiate bean. Field " + name);
                        }
                        break;
                    default:
                }
            }


            log.info("Bean instantiated: {}", bean);
            objectsById.put(bean.getName(), ob);
            objectsByClass.put(bean.getClassName(), ob);

        }
    }

    // конвертирует строку в объект соответствующего
    private Object convert(String typeName, String data) throws Exception {
        switch (typeName) {
            case "int":
            case "Integer":
                return Integer.valueOf(data);
            case "double":
            case "Double":
                return Double.valueOf(data);
            case "boolean":
            case "Boolean":
                return Boolean.valueOf(data);
            case "java.lang.String":
                return String.valueOf(data);
            default:
                throw new InvalidConfigurationException("type name = " + typeName);
        }
    }

    private void parseBean(Node bean) throws InvalidConfigurationException {
        NamedNodeMap attr = bean.getAttributes();
        Node name = attr.getNamedItem(ATTR_BEAN_ID);
        String nameVal = name.getNodeValue();
        String classVal = attr.getNamedItem(ATTR_BEAN_CLASS).getNodeValue();
        log.info("BEAN: [name: {}, class: {}]", nameVal, classVal);

        // ищем все проперти внутри
        NodeList list = bean.getChildNodes();
        Map<String, Property> properties = new HashMap<>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (TAG_PROPERTY.equals(node.getNodeName())) {
                Property property = parseProperty(node);
                log.info("\tSET {}", property);
                properties.put(property.getName(), property);
            }
        }
        //
        beans.add(new Bean(nameVal, classVal, properties));
    }

    private Property parseProperty(Node node) throws InvalidConfigurationException {
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

    private Document readXml(String path) throws InvalidConfigurationException {
        File file = new File(path);
        try {
            log.info("Context configuration xml: " + file.getAbsolutePath());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
    }

    public List<Bean> getBeans() {
        return beans;
    }
}

