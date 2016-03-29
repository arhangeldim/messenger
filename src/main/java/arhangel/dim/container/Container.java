package arhangel.dim.container;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;

    private Map<String, Object> objByName;

    private Map<String, Object> objByClass;


    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {

        // вызываем BeanXmlReader
        BeanXmlReader reader = new BeanXmlReader();
        try {
            beans = reader.parseBeans(pathToConfig);
        } catch (JDOMException | IOException e1) {
            throw new InvalidConfigurationException("Invalid configuration: " + pathToConfig);
        }
        objByName = new HashMap<>();
        objByClass = new HashMap<>();
        BeanGraph graph = new BeanGraph(beans);
        beans = new ArrayList<>();
        for (BeanVertex vertex :
                graph.sort()) {
            beans.add(vertex.getBean());
        }
        for (int i = 0; i < beans.size(); i++) {
            instantiateBean(beans.get(i));
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
        return objByClass.get(className);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {

        // Примерный ход работы
        try {
            String className = bean.getClassName();
            Class clazz = Class.forName(className);
            // ищем дефолтный конструктор
            Object ob = clazz.newInstance();

            for (String name : bean.getProperties().keySet()) {
                // ищем поле с таким именен внутри класса
                // учитывая приватные
                Field field = clazz.getDeclaredField(name);
                // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки

                // Делаем приватные поля доступными
                field.setAccessible(true);

                // Далее определяем тип поля и заполняем его
                // Если поле - примитив, то все просто
                // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
                if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                    field.set(ob,parseValue(field.getType().getName(), bean.getProperties().get(name).getValue()));
                } else {
                    field.set(ob, objByName.get(bean.getProperties().get(name).getValue()));
                }
            }
            objByName.put(bean.getName(), ob);
            objByClass.put(bean.getClassName(), ob);
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Object parseValue(String clazz, String value) {
        switch (clazz) {
            case "boolean":
                return Boolean.valueOf(value);
            case "byte":
                return Byte.valueOf(value);
            case "short":
                return Short.valueOf(value);
            case "int":
                return Integer.valueOf(value);
            case "long":
                return Long.valueOf(value);
            case "double":
                return Double.valueOf(value);
            case "float":
                return Float.valueOf(value);
            default:
                return value;
        }
    }
}
