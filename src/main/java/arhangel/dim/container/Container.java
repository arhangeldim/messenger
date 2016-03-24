package arhangel.dim.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private List<BeanVertex> beansVertex;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws Exception {

        // вызываем BeanXmlReader
        BeanXmlReader xmlReader = new BeanXmlReader();
        BeanGraph graph = new BeanGraph();
        beansVertex = graph.sort(xmlReader.parseBeans(pathToConfig));
    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) throws Exception {
        for (BeanVertex beanVertex : beansVertex) {
            if (beanVertex.getBean().getName().equals(name)) {
                return instantiateBean(beanVertex.getBean());
            }
        }
        return null;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) throws Exception {
        for (BeanVertex beanVertex : beansVertex) {
            return instantiateBean(beanVertex.getBean());
        }
        return null;
    }

    private Object instantiateBean(Bean bean) throws Exception {
        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        Object ob = clazz.newInstance();
        for (String name : bean.getProperties().keySet()) {
            Field field = clazz.getDeclaredField(name);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals("set" + name.substring(0, 1).toUpperCase() + name.substring(1))) {
                    if (bean.getProperties().get(name).getType().equals(ValueType.REF)) {
                        method.invoke(ob, getByName(bean.getProperties().get(name).getValue()));
                    } else {
                        method.invoke(ob, toObject(field.getType(), bean.getProperties().get(name).getValue()));
                    }
                }
            }

        }
        return ob;
    }

    private Object toObject(Class clazz, String value) {
        if (Integer.TYPE == clazz) {
            return Integer.parseInt(value);
        }
        if (Boolean.TYPE == clazz) {
            return Boolean.parseBoolean(value);
        }
        if (Byte.TYPE == clazz) {
            return Byte.parseByte(value);
        }
        if (Short.TYPE == clazz) {
            return Short.parseShort(value);
        }
        if (Long.TYPE == clazz) {
            return Long.parseLong(value);
        }
        if (Float.TYPE == clazz) {
            return Float.parseFloat(value);
        }
        if (Double.TYPE == clazz) {
            return Double.parseDouble(value);
        }
        if (Character.TYPE == clazz) {
            return value.charAt(0);
        }
        return value;
    }

}
