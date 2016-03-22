package arhangel.dim.container;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Html parser to bean class
 * Created by Riv on 15.03.2016.
 */
class BeanXmlReader {

    private static final String TAG_BEAN = "bean";
    private static final String TAG_PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "val";
    public static final String ATTR_REF = "ref";
    private static final String ATTR_BEAN_ID = "id";
    private static final String ATTR_BEAN_CLASS = "class";

    public static List<Bean> parseBeans(String pathToFile) throws IOException, ClassNotFoundException {
        List<Bean> result = new ArrayList<>();
        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(new File(pathToFile), "ISO-8859-1");
        } catch (FileNotFoundException exept) {
            throw new IOException("can not access the file");
        }

        List<Element> classList = htmlFile.getElementsByTag(TAG_BEAN);
        for (Element el : classList) {
            Map<String, Property> properties = new HashMap<>();
            for (Element pr : el.getElementsByTag(TAG_PROPERTY)) {
                String name = pr.attr(ATTR_NAME);
                if (!pr.attr(ATTR_REF).isEmpty()) {
                    properties.put(name, new Property(name, pr.attr(ATTR_REF), ValueType.REF));
                } else if (!pr.attr(ATTR_VALUE).isEmpty()) {
                    properties.put(name, new Property(name, pr.attr(ATTR_VALUE), ValueType.VAL));
                } else {
                    throw new ClassNotFoundException("incorrect attribute");
                }
            }
            result.add(new Bean(el.attr(ATTR_BEAN_ID),
                    el.attr(ATTR_BEAN_CLASS), properties));
        }
        return result;
    }

}