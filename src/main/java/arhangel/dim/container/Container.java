package arhangel.dim.container;

import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/*
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */

public class Container {
    private Map<Bean, Object> createdObjects;
    private List<Bean> beans;

    /*
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        createdObjects = new HashMap<>();

        // вызываем BeanXmlReader
        BeanXmlReader reader = new BeanXmlReader();
        try {
            beans = reader.parseBeans(pathToConfig);
        } catch (ParserConfigurationException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }

        BeanGraph graph = new BeanGraph();

        for (Bean bean : beans) {
            graph.addAllEdges(bean);
        }

        List<BeanVertex> sortedVertices = null;
        try {
            sortedVertices = graph.topologicalSort();
        } catch (CycleReferenceException e) {
            e.printStackTrace();
        }

        for (Bean bean : beans) {
            sortedVertices.add(new BeanVertex(bean));
        }

        for (BeanVertex vertex : sortedVertices) {
            instantiateBean(vertex.getBean());
        }
    }

    /*
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        for (Bean currentBean : createdObjects.keySet()) {
            if (currentBean.getName().equals(name)) {
                return createdObjects.get(currentBean);
            }
        }
        return null;
    }

    /*
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        for (Bean currentbean : createdObjects.keySet()) {
            if (currentbean.getClassName().equals(className)) {
                return createdObjects.get(currentbean);
            }
        }
        return null;
    }


    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        String className = bean.getClassName();
        Class clazz = null;
        Object beanObject = null;

        try {
            clazz = Class.forName(className);
            beanObject = clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new InvalidConfigurationException(String.format("Ошибка  XML: " +
                    "класс %s не может быть загружен!", className));
        }

        for (String name : bean.getProperties().keySet()) {
            // ищем поле с таким именен внутри класса
            // учитывая приватные
            Field field = null;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                throw new InvalidConfigurationException(String.format("Ошибка  XML: " +
                        "поле %s класса %s не существует!", name, className));
            }
            // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки

            // Делаем приватные поля доступными
            field.setAccessible(true);

            try {
                if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                    Integer valueInteger = Integer.valueOf(bean.getProperties().get(name).getValue());
                    int value = valueInteger.intValue();
                    field.set(beanObject, value);

                } else {
                    Object referedObject = getByName(bean.getProperties().get(name).getValue());
                    field.set(beanObject, referedObject);

                }
            } catch (IllegalAccessException e) {
                throw new InvalidConfigurationException(String.format("Ошибка доступа" +
                        "к полю %s класса %s", name, className));
            }
        }
        createdObjects.put(bean, beanObject);
    }
}
