package arhangel.dim.container;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.naming.NameNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BeanXmlReader {
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public static List<Bean> parseBeans(String pathToFile) throws IOException, ParserConfigurationException, SAXException,
            NameNotFoundException {
        List<Bean> parseResult = new ArrayList<>();
        File inputXmlFile = new File(pathToFile);
        if (!inputXmlFile.exists()) {
            throw new FileNotFoundException("Incorrect input file");
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document xmlDocument = documentBuilder.parse(inputXmlFile);
        NodeList listOfBeanNodes = xmlDocument.getElementsByTagName(TAG_BEAN);
        //поочередно разбираем данные о bean
        for (int i = 0; i < listOfBeanNodes.getLength(); ++i) {
            Node node = listOfBeanNodes.item(i);
            Element nextElement = (Element) node;
            NodeList propertiesList = nextElement.getElementsByTagName(TAG_PROPERTY);
            Map<String, Property> properties = new HashMap<>();
            //парсим properties
            for (int j = 0; j < propertiesList.getLength(); ++j) {
                Element nextProperty = (Element) propertiesList.item(j);
                String name = nextProperty.getAttribute(ATTR_NAME);
                Property nextValue;
                if (nextProperty.hasAttribute(ATTR_VALUE)) {
                    nextValue = new Property(name, nextProperty.getAttribute(ATTR_VALUE), ValueType.VAL);
                } else if (nextProperty.hasAttribute(ATTR_REF)) {
                    nextValue = new Property(name, nextProperty.getAttribute(ATTR_REF), ValueType.REF);
                } else {
                    throw new NameNotFoundException("Incorrect name of attribute");
                }
                properties.put(name, nextValue);

            }
            Bean nextBean = new Bean(nextElement.getAttribute(ATTR_BEAN_ID), nextElement.getAttribute(ATTR_BEAN_CLASS),
                    properties);
            parseResult.add(nextBean);
        }
        return parseResult;

    }
}
