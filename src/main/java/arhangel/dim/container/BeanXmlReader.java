package arhangel.dim.container;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.naming.NameNotFoundException;

public class BeanXmlReader {
    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public List<Bean> parseBeans(String pathToFile) throws IOException, JDOMException, NameNotFoundException {
        List<Bean> resultList = new ArrayList<>();
        SAXBuilder saxBuilder = new SAXBuilder();
        File xmlFile = new File(pathToFile);
        if (!xmlFile.exists()) {
            throw new FileNotFoundException("INCORRECT PATH TO FILE");
        }
        Document document = saxBuilder.build(xmlFile);
        Element classElement = document.getRootElement();
        List<Element> classList = classElement.getChildren(TAG_BEAN);
        for (Element el: classList) {
            Map<String, Property> properties = new HashMap<>();
            for (Element pr: el.getChildren(TAG_PROPERTY)) {
                String name = pr.getAttributeValue(ATTR_NAME);
                if (pr.getAttributeValue(ATTR_REF) != null) {
                    properties.put(name, new Property(name, pr.getAttributeValue(ATTR_REF), ValueType.REF));
                } else if (pr.getAttributeValue(ATTR_VALUE) != null) {
                    properties.put(name, new Property(name, pr.getAttributeValue(ATTR_VALUE), ValueType.VAL));
                } else {
                    throw new NameNotFoundException("INCORRECT PROPERTY ATTRIBUTE");
                }
            }
            resultList.add(new Bean(el.getAttributeValue(ATTR_BEAN_ID),
                    el.getAttributeValue(ATTR_BEAN_CLASS), properties));
        }
        return resultList;
    }
}
