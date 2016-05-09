package arhangel.dim.container;



import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitriy on 11.03.16.
 * Рассчитывается, что файл config.xml строго типизирован, например, с помощью .xsd
 */

public class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    private static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    private static final boolean ignoreWhitespace = true; //игнорировать пустые строки
    private static final boolean ignoreComments = true; //игнорировать комментарии
    private static final boolean putCDATAIntoText = false;
    private static final boolean createEntityRefs = false;


    public List<Bean> parseBeans(String filename) throws Exception {
        List<Bean> result = new ArrayList<Bean>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setIgnoringComments(ignoreComments);
        dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
        dbf.setCoalescing(putCDATAIntoText);
        dbf.setExpandEntityReferences(createEntityRefs);

        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new MyErrorHandler(new PrintWriter(System.err, true)));
        Document doc = db.parse(new File(filename));

        NodeList nodes = doc.getElementsByTagName(TAG_BEAN);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node beanNode = nodes.item(i);
            Bean temp = new Bean(null, null,null);

            if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attrs = beanNode.getAttributes();
                temp.setName(attrs.getNamedItem(ATTR_BEAN_ID).getNodeValue());
                temp.setClassName(attrs.getNamedItem(ATTR_BEAN_CLASS).getNodeValue());

                if (beanNode.hasChildNodes()) {
                    NodeList propertiesNodes = beanNode.getChildNodes();
                    Map<String, Property> properties = new HashMap<String, Property>();

                    for (int j = 0; j < propertiesNodes.getLength(); j++) {
                        Node propertyNode = propertiesNodes.item(j);

                        if (propertyNode.getNodeName().equals(TAG_PROPERTY)) {
                            NamedNodeMap propertyAttrs = propertyNode.getAttributes();
                            ValueType type = ValueType.VAL;
                            String fieldType;

                            if (propertyAttrs.getNamedItem(ATTR_REF) != null) {
                                type = ValueType.REF;
                                fieldType = propertyAttrs.getNamedItem(ATTR_REF).getNodeValue();
                            } else {
                                fieldType = propertyAttrs.getNamedItem(ATTR_VALUE).getNodeValue();
                            }

                            properties.put(propertyAttrs.getNamedItem(ATTR_NAME).getNodeValue(),
                                    new Property(propertyAttrs.getNamedItem(ATTR_NAME).getNodeValue(),
                                            fieldType,
                                            type));

                            temp.setProperties(properties);
                        }
                    }
                }
            }
            result.add(temp);
        }
        return result;
    }

    private class MyErrorHandler implements ErrorHandler {

        private PrintWriter out;

        MyErrorHandler(PrintWriter out) {
            this.out = out;
        }

        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() +
                    ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
}
