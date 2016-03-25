package arhangel.dim.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Object> objectsByName = new HashMap<>();
    private Map<String, Object> objectsByClass = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        BeanXmlReader xmlReader = new BeanXmlReader();
        try {
            beans = xmlReader.parseBeans(pathToConfig);
        } catch (Exception ex) {
            throw new InvalidConfigurationException(ex.getMessage());
        }

        List<BeanVertex> sortVertices = beanSort();
        if (sortVertices == null) {
            throw new InvalidConfigurationException("Beans cycled");
        }
        try {
            for (BeanVertex v : sortVertices) {
                instantiateBean(v.getBean());
            }
        } catch (Exception ex) {
            throw new InvalidConfigurationException(ex.getMessage());
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        return objectsByName.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return objectsByClass.get(className);
    }

    private List<BeanVertex> beanSort() throws InvalidConfigurationException {
        BeanGraph graph = new BeanGraph();
        Map<String, BeanVertex> vertexByName = new HashMap<>();
        for (Bean bean : beans) {
            vertexByName.put(bean.getName(), graph.addVertex(bean));
        }
        for (Bean bean : beans) {
            for (Map.Entry<String, Property> prop : bean.getProperties().entrySet()) {
                if (prop.getValue().getType() == ValueType.REF) {
                    BeanVertex from = vertexByName.get(bean.getName());
                    BeanVertex to = vertexByName.get(prop.getValue().getValue());
                    if (to == null) {
                        throw new InvalidConfigurationException("WTF?");
                    }
                    graph.addEdge(from, to);
                }
            }
        }
        return graph.topSort();
    }

    private void instantiateBean(Bean bean) throws Exception {
        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        // ищем дефолтный конструктор
        Object ob = clazz.newInstance();
        for (String name : bean.getProperties().keySet()) {
            // ищем поле с таким именен внутри класса
            // учитывая приватные
            Field field = clazz.getDeclaredField(name);
            // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки
            if (field == null) {
                throw new InvalidConfigurationException("Class " + className + " doesn't have field " + name);
            }
            // Делаем приватные поля доступными
            field.setAccessible(true);
            Property prop = bean.getProperties().get(name);
            Class<?> type = field.getType();
            Method setter = clazz.getDeclaredMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1),
                    type);
            if (setter == null) {
                throw new InvalidConfigurationException("Class " + className + " doesn't have setter for field " + name);
            }
            switch (prop.getType()) {
                case VAL:
                    //field.set(ob, convert(type.getTypeName(), prop.getValue()));
                    setter.invoke(ob, convert(type.getTypeName(), prop.getValue()));
                    break;
                case REF:
                    String refName = prop.getValue();
                    if (objectsByName.containsKey(refName)) {
                        //field.set(ob, objectsByName.get(refName));
                        setter.invoke(ob, objectsByName.get(refName));
                    } else {
                        throw new InvalidConfigurationException("Failed to instantiate bean. Field " + name);
                    }
                    break;
                default:
            }
            objectsByName.put(bean.getName(), ob);
            objectsByClass.put(bean.getClassName(), ob);
        }

    }

    // конвертирует строку в объект соответствующего типа
    private Object convert(String typeName, String data) throws Exception {
        switch (typeName) {
            case "int":
            case "Integer":
                return Integer.valueOf(data);
            case "double":
            case "Double":
                return Double.valueOf(data);
            case "boolean":
            case "Boolean":
                return Boolean.valueOf(data);
            default:
                throw new InvalidConfigurationException("type name = " + typeName);
        }
    }


}
