package arhangel.dim.container;

import arhangel.dim.container.beans.Engine;
import arhangel.dim.container.beans.Gear;
import org.jdom2.JDOMException;

import javax.naming.NameNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
                String methodName = "set" + this.getMethodName(property.getName());
                try {
                    if (property.getType() == ValueType.VAL) {
                        Class fieldType = clazz.getDeclaredField(propertyName).getType();
                        Method method = clazz.getMethod(methodName, fieldType);
                        method.invoke(ob, convert(fieldType.getTypeName(), property.getValue()));
                    } else if (property.getType() == ValueType.REF) {
                        Object parameter = getByName(property.getValue());
                        Method method = clazz.getMethod(methodName, parameter.getClass());
                        method.invoke(ob, parameter);
                    }
                } catch (NoSuchMethodException | InvocationTargetException e) {
                    throw new InvalidConfigurationException("ERROR: method " + methodName +
                            "doesn't exist or can't invoke");
                } catch (NoSuchFieldException e) {
                    throw new InvalidConfigurationException("ERROR: filed " + propertyName +
                            " doesn't exist" + e.getMessage());
                }
            }
            objByClassName.put(bean.getClassName(), ob);
            objByName.put(bean.getName(), ob);

        } catch (ClassNotFoundException e) {
            throw new InvalidConfigurationException("ERROR: class " + className +
                    " doesn't implement " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidConfigurationException("INSTANCE ERROR: " + className + e.getMessage());
        }
    }

    private String getMethodName(String str) {
        return Character.toString(str.charAt(0)).toUpperCase() + str.substring(1);
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
