package arhangel.dim.container;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by eaglesh on 19.03.16.
 */
public class BeanXmlReader {
    public Map<String,Bean> parseBeans(String filePath) throws InvalidConfigurationException {

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {

            return parseBeans(inputStream);

        } catch (IOException e) {
            throw new InvalidConfigurationException(e);
        }
    }

    public Map<String,Bean> parseBeans(InputStream inputStream) throws InvalidConfigurationException {
        try {
            //set singular classloader to all future classes
            //Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());

            JAXBContext context = JAXBContext.newInstance(Bean.Beans.class, Bean.class);

            Bean.Beans beans = (Bean.Beans) context.createUnmarshaller().unmarshal(inputStream);

            Map<String, Bean> result = beans.getBeans().stream().collect(Collectors.toMap(Bean::getName, bean -> bean));
            return result;
        } catch (JAXBException e) {
            throw new InvalidConfigurationException(e);
        }
    }

}
