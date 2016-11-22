package arhangel.dim.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {

        // вызываем BeanXmlReader
        BeanXmlReader xmlReader = new BeanXmlReader();
        beans = xmlReader.sortBeans(xmlReader.parseBeans(pathToConfig));
    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) throws Exception {
        List<Bean> list = beans.stream().filter(bean -> bean.getName().equals(name)).collect(Collectors.toList());
        if (list.size() == 0) {
            return null;
        }
        return instantiateBean(list.get(0));
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) throws Exception {
        List<Bean> list = beans
                .stream()
                .filter(bean -> bean.getClassName().equals(className))
                .collect(Collectors.toList());
        if (list.size() == 0) {
            return null;
        }
        return instantiateBean(list.get(0));
    }

    private Object instantiateBean(Bean bean) throws Exception {

        if (bean.getInstance() != null) {
            return bean.getInstance();
        }

        String className = bean.getClassName();
        Class clazz = Class.forName(className);

        Object obj = clazz.newInstance();

        for (String name : bean.getProperties().keySet()) {
            Field field = clazz.getDeclaredField(name);

            List<Method> methodList = Arrays.stream(clazz.getMethods())
                    .filter(method
                            -> method.getName()
                            .equals("set" + name.substring(0, 1).toUpperCase() + name.substring(1)))
                    .collect(Collectors.toList());

            if (methodList.size() == 0) {
                throw new Exception();
            }
            
            Method setter = methodList.get(0);

            if (bean.getProperties().get(name).getType().equals(ValueType.REF)) {
                setter.invoke(obj, getByName(bean.getProperties().get(name).getValue()));
            } else {
                setter.invoke(obj, toObject(field.getType(), bean.getProperties().get(name).getValue()));
            }
        }
        bean.setInstance(obj);
        return obj;
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
