package arhangel.dim.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {
    private Map<Bean, Object> createdObjects;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        createdObjects = new HashMap<>();
        List<Bean> parsedBeans = new BeanXmlReader().parseBeans(pathToConfig);
        BeanGraph beans = new BeanGraph();

        for (Bean bean : parsedBeans) {
            beans.addVertex(bean);
        }
        beans.updateGraphLinks();

        List<BeanVertex> sortedBeans = beans.getSortedVertexes();
        for (int i = 0; i < sortedBeans.size(); i++) {
            instantiateBean(sortedBeans.get(i).getBean());
        }
    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        for (Bean currentBean : createdObjects.keySet()) {
            if (currentBean.getName().equals(name)) {
                return createdObjects.get(currentBean);
            }
        }
        return null;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        for (Bean currentBean : createdObjects.keySet()) {
            if (currentBean.getClassName().equals(className)) {
                return createdObjects.get(currentBean);
            }
        }
        return null;
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {

        String className = bean.getClassName();
        Class beanClass = null;
        Object beanObject = null;
        try {
            beanClass = Class.forName(className);
            beanObject = beanClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new InvalidConfigurationException(String.format("Ошибка в XML конфиге: класс %s не может быть загружен!", className));
        }

        for (Property currentProperty : bean.getProperties().values()) {
            String propertyName = currentProperty.getName();
            propertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            String setterMethodName = String.format("set%s", propertyName);
            Method propertyMethod = null;
            try {
                if (currentProperty.getType() == ValueType.REF) {
                    Object referredObject = getByName(currentProperty.getValue());
                    propertyMethod = beanClass.getDeclaredMethod(
                            setterMethodName, referredObject.getClass());
                    propertyMethod.invoke(beanObject, referredObject);
                } else {
                    String value = currentProperty.getValue();
                    propertyMethod = beanClass.getDeclaredMethod(
                            setterMethodName, Integer.TYPE);
                    propertyMethod.invoke(beanObject, Integer.parseInt(value));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new InvalidConfigurationException(String.format("Ошибка в XML конфиге: метода %s в классе %s не найдено!",
                        setterMethodName, bean.getClassName()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new InvalidConfigurationException(String.format("Ошибка в XML конфиге: метод %s  в классе %s не может быть вызван!",
                        setterMethodName, bean.getClassName()));
            }
        }

        createdObjects.put(bean, beanObject);

    }
}
