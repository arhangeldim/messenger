package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Bean> result = new ArrayList<>();

        Document document;
        try {
            document = readXml(pathToFile);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }

        Node node = document.getChildNodes().item(0); //получаем корневой каталог
        NodeList beanList = node.getChildNodes();      //получаем структуру для парсинга

        for (int i = 0; i < beanList.getLength(); i++) {
            if (beanList.item(i).getNodeName().equals(TAG_BEAN)) { //если встречаем ноду с тэйгом bean
                String nameBean = null;
                String classNameBean = null;
                Map<String, Property> properties = new HashMap<>();


                //парсим имя переменной и класс
                NamedNodeMap namedNodeMap = beanList.item(i).getAttributes();
                for (int j = 0; j < namedNodeMap.getLength(); j++) {
                    if (namedNodeMap.item(j).getNodeName().equals(ATTR_BEAN_CLASS)) {
                        classNameBean = namedNodeMap.item(j).getNodeValue();
                    } else if (namedNodeMap.item(j).getNodeName().equals(ATTR_BEAN_ID)) {
                        nameBean = namedNodeMap.item(j).getNodeValue();
                    }
                }

                NodeList propertyList = beanList.item(i).getChildNodes();
                for (int j = 0; j < propertyList.getLength(); j++) {
                    String nameProperty = null; // Имя поля
                    String valueProperty = null; // Значение поля
                    ValueType typeProperty = null; // Метка ссылочное значение или примитив
                    if (propertyList.item(j).getNodeName().equals(TAG_PROPERTY)) {
                        for (int k = 0; k < propertyList.item(j).getAttributes().getLength(); k++) {
                            if (propertyList.item(j).getAttributes().item(k).getNodeName().equals(ATTR_NAME)) {
                                nameProperty = propertyList.item(j).getAttributes().item(k).getNodeValue();
                            } else if (propertyList.item(j).getAttributes().item(k).getNodeName().equals(ATTR_VALUE)) {
                                valueProperty = propertyList.item(j).getAttributes().item(k).getNodeValue();
                                if (typeProperty == ValueType.REF) {
                                    System.out.println("Property cann't have attributes VAL and REF");
                                    throw new IllegalArgumentException();
                                }
                                typeProperty = ValueType.VAL;
                            } else if (propertyList.item(j).getAttributes().item(k).getNodeName().equals(ATTR_REF)) {
                                valueProperty = propertyList.item(j).getAttributes().item(k).getNodeValue();
                                if (typeProperty == ValueType.VAL) {
                                    System.out.println("Property cann't have attributes VAL and REF");
                                    throw new IllegalArgumentException();
                                }
                                typeProperty = ValueType.REF;
                            }
                        }
                        if (nameProperty != null) {
                            properties.put(nameProperty,new Property(nameProperty,valueProperty,typeProperty));
                        }
                    }
                }
                if (nameBean != null) {
                    result.add(new Bean(nameBean,classNameBean,properties));
                }
            }
        }

        return result;
    }

    private Document readXml(String pathToFile) throws Exception {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setValidating(false);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(new File(pathToFile));
        } catch (Exception exception) {
            String message = "XML parsing error!";
            throw new Exception(message);
        }
    }

    public static void main(String[] args) {
        List<Bean> list = new BeanXmlReader().parseBeans("C:\\temp\\java\\mailru\\messenger\\config.xml");
        list.forEach(System.out::println);
    }

}
