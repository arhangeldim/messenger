package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.naming.NameNotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Container {
    private List<Bean> beans;
    private Map<String, Object> objectByName;
    private Map<String, Object> objectByClassName;
    /**
     * @throws InvalidConfigurationException - неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        objectByClassName = new HashMap<>();
        objectByName = new HashMap<>();
        List<Bean> beansFromXML;
        try {
            beansFromXML = BeanXmlReader.parseBeans(pathToConfig);
        } catch (IOException e) {
            throw new InvalidConfigurationException(e.getMessage());
        } catch (ParserConfigurationException e) {
            throw new InvalidConfigurationException("I can't parse configuration. " + e.getMessage());
        } catch (SAXException e) {
            throw new InvalidConfigurationException("SAX parse " + e.getMessage());
        } catch (NameNotFoundException e) {
            throw new InvalidConfigurationException("Input file does not exist. " + e.getMessage());
        }
        List<BeanVertex> tmp = (new BeanGraph(beansFromXML).topSort());
        beans = new ArrayList<>();
        for (BeanVertex beanVertex : tmp) {
            beans.add(beanVertex.getBean());
        }
        init(beans);
    }

    private void init(List<Bean> beans) {
        for (int i = 0; i < beans.size(); ++i) {
            initBean(beans.get(i));
        }
    }

    private void initBean(Bean bean) {
        String name = bean.getClassName();
        Class clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            System.err.println("Container class in initBean - incorrect name of class " + e.getMessage());
            return;
        }
        Object object = null;
        try {
            object = clazz.newInstance();
        } catch (InstantiationException e) {
            System.err.println("Can't initialize class " + e.getMessage());
            return;
        } catch (IllegalAccessException e) {
            System.err.print(e.getMessage());
            return;
        }
        for (Property property : bean.getProperties().values()) {
            String methodName = "set" + Utils.capitalize(property.getName());
            try {
                if (property.getType() == ValueType.REF) {
                    Object parameter = getByName(property.getValue());
                    Method method = clazz.getMethod(methodName,
                            parameter.getClass());
                    method.invoke(object, parameter);
                } else {
                    Method method = clazz.getMethod(methodName,
                            int.class);
                    method.invoke(object, new Integer(property.getValue()));
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        objectByClassName.put(bean.getClassName(), object);
        objectByClassName.put(bean.getName(), object);

    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        return objectByName.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return objectByClassName.get(className);
    }

}
