package arhangel.dim.container;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by olegchuikin on 18/03/16.
 */
public class Container {
    private List<Bean> beans;

    private Map<String, Object> objects;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws BeanXmlReader.CycleReferenceException - неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.beans = new BeanXmlReader().parseBeans(pathToConfig);

        objects = new HashMap<>();

        for (Bean bean : beans) {
            objects.put(bean.getName(), convertBeanToObject(bean));
        }
    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) throws IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException {
        return objects.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {

        for (Object o : objects.values()) {
            if (o.getClass().getName().equals(className)) {
                return o;
            }
        }

        return null;
    }

    private Object convertBeanToObject(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Class clazz = Class.forName(bean.getClassName());
        Object result = clazz.newInstance();

        Collection<Property> properties = bean.getProperties().values();

        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                Property property = findPropertyByName(properties, method.getName().substring(3).toLowerCase());
                if (property != null) {
                    if (property.getType().equals(ValueType.REF)) {
                        method.invoke(result, getByName(property.getValue()));
                    } else {
                        Object arg = parseArgument(method.getParameterTypes()[0], property.getValue());
                        method.invoke(result, arg);
                    }
                }
            }
        }

        return result;
    }

    private Property findPropertyByName(Collection<Property> properties, String name) {
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    private Object parseArgument(Class clazz, String value) {
        if (clazz.equals(int.class)) {
            return Integer.parseInt(value);
        }
        if (clazz.equals(long.class)) {
            return Long.parseLong(value);
        }
        if (clazz.equals(double.class)) {
            Double.parseDouble(value);
        }
        if (clazz.equals(float.class)) {
            Float.parseFloat(value);
        }
        if (clazz.equals(String.class)) {
            return value;
        }
        return null;
    }

}
