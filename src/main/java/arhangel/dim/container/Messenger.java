package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.naming.NameNotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public class Messenger {
    public static void main(String[] arg) {
        BeanXmlReader xmlReader = new BeanXmlReader();
        List<Bean> beans;

        String path = "C:\\Users\\Дмитрий\\Documents\\technotrack\\java\\messenger" +
                        "\\src\\main\\java\\arhangel\\dim\\container\\test";
        try {
            beans = xmlReader.parseBeans(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        } catch (ParserConfigurationException e) {
            System.out.println("Parsing xml-file" + e.getMessage());
            return;
        } catch (SAXException e) {
            e.printStackTrace();
            return;
        } catch (NameNotFoundException e) {
            System.out.println("Please add attribute (ref or val) " + e.getMessage());
            return;
        }

        for (Bean bean : beans) {
            System.out.println(bean.toString());
        }


    }
}