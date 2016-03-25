package arhangel.dim.container;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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

    public List<Bean> parseBeans(String pathToFile) throws IOException, JDOMException, InvalidConfigurationException {
        SAXBuilder saxBuilder = new SAXBuilder();
        File inputFile = new File(pathToFile);
        if (!inputFile.exists()) {
            throw new FileNotFoundException("File " + pathToFile + " doesn't exist");
        }
        Document document = saxBuilder.build(inputFile);
        Element rootElement = document.getRootElement();
        List<Element> beanList = rootElement.getChildren();
        List<Bean> classList = new ArrayList<>();
        for (Element bean : beanList) {
            if (!bean.getName().equals(TAG_BEAN)) {
                throw new InvalidConfigurationException("Incorrect tag: " + bean.getName());
            }
            if (bean.getAttributeValue(ATTR_BEAN_CLASS) == null || bean.getAttributeValue(ATTR_BEAN_ID) == null) {
                throw new InvalidConfigurationException("Bean should have both class and id attributes");
            }
            if (classList.contains(bean.getAttributeValue(ATTR_BEAN_ID))) {
                throw new InvalidConfigurationException("All beans should have unique ids");
            }
            List<Element> propertyList = bean.getChildren();
            Map<String, Property> propertyMap = new HashMap<>();
            for (Element prop : propertyList) {
                if (!prop.getName().equals(TAG_PROPERTY)) {
                    throw new InvalidConfigurationException("Incorrect property: " + prop.getName());
                }
                if (prop.getAttributes().size() > 2) {
                    throw new InvalidConfigurationException("Property should have only 2 attributes: " +
                            ATTR_NAME + " and " + ATTR_REF + " or " + ATTR_VALUE);
                }
                String name = prop.getAttributeValue(ATTR_NAME);
                if (name == null) {
                    throw new InvalidConfigurationException("Incorrect property name");
                }
                if (prop.getAttributeValue(ATTR_VALUE) != null) {
                    propertyMap.put(name, new Property(name, prop.getAttributeValue(ATTR_VALUE), ValueType.VAL));
                }  else if (prop.getAttributeValue(ATTR_REF) != null) {
                    propertyMap.put(name, new Property(name, prop.getAttributeValue(ATTR_REF), ValueType.REF));
                } else {
                    throw new InvalidConfigurationException("Incorrect property attribute. " + name +
                            " should have " + ATTR_REF + " or " + ATTR_NAME);
                }
            }
            classList.add(new Bean(bean.getAttributeValue(ATTR_BEAN_ID),
                    bean.getAttributeValue(ATTR_BEAN_CLASS), propertyMap));
        }
        return classList;
    }
}
