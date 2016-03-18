package arhangel.dim.container;

import arhangel.dim.container.beans.Engine;
import arhangel.dim.container.beans.Gear;
import org.jdom2.JDOMException;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Object> objByName;
    private Map<String, Object> objByClassName;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {

        objByClassName = new HashMap<>();
        objByName = new HashMap<>();
        BeanXmlReader xmlReader = new BeanXmlReader();
        List<Bean> beansXml = null;
        try {
            beansXml = xmlReader.parseBeans(pathToConfig);
        } catch (IOException e) {
            throw new InvalidConfigurationException("FILE READ ERROR: " + e.getMessage());
        } catch (NameNotFoundException e) {
            throw new InvalidConfigurationException("FILE PARSE ERROR: " + e.getMessage());
        } catch (JDOMException e) {
            throw new InvalidConfigurationException("SAX BUILD ERROR: " + e.getMessage());
        }

        BeanGraph graph = new BeanGraph(beansXml);
        beans = graph.getSortedBeans();
        for (Bean bean: beans) {
            instantiateBean(bean);
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {

        return objByName.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {

        return objByClassName.get(className);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {

        String className = bean.getClassName();
        try {
            Class clazz = Class.forName(className);
            Object ob = clazz.newInstance();
            for (Property property: bean.getProperties().values()) {

                String propertyName = property.getName();
                Field field;
                try {
                    field = clazz.getDeclaredField(propertyName);
                } catch (NoSuchFieldException e) {
                    throw new InvalidConfigurationException("ERROR: filed " + propertyName +
                            " doesn't exist" + e.getMessage());
                }
                field.setAccessible(true);
                Class fieldType = field.getType();
                if (property.getType() == ValueType.VAL) {
                    field.set(ob, convert(fieldType.getTypeName(), property.getValue()));
                } else if (property.getType() == ValueType.REF) {
                    Object parameter = getByName(property.getValue());
                    field.set(ob, parameter);
                }
            }
            objByClassName.put(bean.getClassName(), ob);
            objByName.put(bean.getName(), ob);

        } catch (ClassNotFoundException e) {
            throw new InvalidConfigurationException("ERROR: class " + className +
                    " doesn't implement " + e.getMessage());
        } catch (InstantiationException e) {
            throw new InvalidConfigurationException("INSTANCE ERROR: " + className + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new InvalidConfigurationException("INSTANCE ERROR: " + className + e.getMessage());
        }
    }

    private Object convert(String type, String str) throws InvalidConfigurationException {

        switch (type) {
            case "int":
                return Integer.valueOf(str);
            case "long":
                return Long.valueOf(str);
            case "double":
                return Double.valueOf(str);
            case "String":
                return String.valueOf(str);
            default:
                throw new InvalidConfigurationException("The type can be only int/long/double/String");
        }
    }
}
