package ivanov.mikhail.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {


    private static Logger log = LoggerFactory.getLogger(Container.class);

    private List<Bean> beans;

    private Map<String, Object> objectsById = new HashMap<>();
    private Map<String, Object> objectsByClass = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {

        try {

            BeanXmlReader beanXmlReader = new BeanXmlReader();
            beanXmlReader.parseBeans(pathToConfig);
            this.beans = beanXmlReader.getBeans();

            Map<String, BeanVertex> vertexByNamed = new HashMap<>();

            BeanGraph beanGraph = new BeanGraph();
            for (Bean bean : beans) {
                //log.info("ADD VERTEX " + bean.getName());
                vertexByNamed.put(bean.getName(), beanGraph.addVertex(bean));
            }

            for (Bean bean : beans) {
                for (Property property : bean.getProperties().values()) {
                    if (property.getType() == ValueType.REF) {
                        beanGraph.addEdge(vertexByNamed.get(bean.getName()), vertexByNamed.get(property.getValue()));
                    }
                }
            }

            beans = beanGraph.topSort();

            for (Bean bean : beans) {
                //log.info("INSTANT:" + bean.getName());
                instantiateBean(bean);
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
        return objectsById.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return objectsByClass.get(className);
    }

    private void instantiateBean(Bean bean) throws Exception {

        // Примерный ход работы

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
                throw new InvalidConfigurationException("Failed to set field [" + name + "] for class " + clazz.getName());
            }

            Property prop = bean.getProperties().get(name);

            // Делаем приватные поля доступными
            field.setAccessible(true);

            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
            // храним тип данных
            Type type = field.getType();

            Method method = clazz.getDeclaredMethod("set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), field.getType());

            switch (prop.getType()) {
                case VAL:
                    method.invoke(ob, convert(type.getTypeName(), prop.getValue()));
                    break;
                case REF:
                    String refName = prop.getValue();
                    if (objectsById.containsKey(refName)) {
                        method.invoke(ob, objectsById.get(refName));
                    } else {
                        throw new InvalidConfigurationException("Failed to instantiate bean. Field " + name);
                    }
                    break;
                default:
            }

        }

        objectsById.put(bean.getName(), ob);
        objectsByClass.put(bean.getClassName(), ob);
    }

    // конвертирует строку в объект соответствующего
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
