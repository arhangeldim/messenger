package arhangel.dim.container;

/**
 * Created by dmitriy on 18.03.16.
 */

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;


public class Container {
    private List<Bean> beans;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException - неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, IOException, SAXException, ParserConfigurationException {
        this.beans = BeanXmlReader.parseBeans(pathToConfig);
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        return null;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return null;
    }

}
