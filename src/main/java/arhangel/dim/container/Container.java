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
    private Map<String, Object> objectByName;
    private  Map<String, Object> objectByClass;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        try {
            objectByName = new HashMap<>();
            objectByClass = new HashMap<>();

            BeanXmlReader xmlReader = new BeanXmlReader();
            beans = xmlReader.parseBeans(pathToConfig);

            BeanGraph graph = new BeanGraph();
            Map<String, BeanVertex> beanVertexByName = new HashMap<>();
            for (Bean bean : beans) {
                beanVertexByName.put(bean.getName(), graph.addVertex(bean));
            }

            for (Bean bean : beans) {
                BeanVertex fromVertex = beanVertexByName.get(bean.getName());
                for (Property property : bean.getProperties().values()) {
                    if (property.getType() == ValueType.REF) {
                        BeanVertex toVertex = beanVertexByName.get(property.getValue());
                        graph.addEdge(fromVertex, toVertex);
                    }
                }
            }
            List<BeanVertex> beansSorted = graph.sort();
            for (BeanVertex beanVertex : beansSorted) {
                instantiateBean(beanVertex.getBean());
            }

        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }
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
        return objectByClass.get(className);
    }

    private String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {

        String className = bean.getClassName();
        Class clazz;
        Object ob;

        try {
            clazz = Class.forName(className);
            ob = clazz.newInstance();
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }

        try {
            Property property;
            for (Map.Entry<String, Property> entry : bean.getProperties().entrySet()) {
                property = entry.getValue();
                String name = entry.getKey();

                String setterName = "set" + capitalizeFirstLetter(name);
                Method setter = null;
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(setterName)) {
                        setter = method;
                        break;
                    }
                }

                if (setter == null) {
                    throw new InvalidConfigurationException("No such method");
                }

                if (property.getType() == ValueType.REF) {
                    Object toSet = getByName(property.getValue());
                    setter.invoke(ob, toSet);
                } else {
                    Field field = clazz.getDeclaredField(name);
                    String value = property.getValue();
                    switch (field.getType().getName()) {
                        case "Boolean":
                        case "boolean":
                            setter.invoke(ob, Boolean.parseBoolean(value));
                            break;
                        case "byte":
                        case "Byte":
                            setter.invoke(ob, Byte.parseByte(value));
                            break;
                        case "int":
                        case "Integer":
                            setter.invoke(ob, Integer.parseInt(value));
                            break;
                        case "short":
                        case "Short":
                            setter.invoke(ob, Short.parseShort(value));
                            break;
                        case "long":
                        case "Long":
                            setter.invoke(ob, Long.parseLong(value));
                            break;
                        case "float":
                        case "Float":
                            setter.invoke(ob, Float.parseFloat(value));
                            break;
                        case "double":
                        case "Double":
                            setter.invoke(ob, Double.parseDouble(value));
                            break;
                        case "String":
                            setter.invoke(ob, value);
                            break;
                        default:
                            throw new Exception("cannot set the field " + field.toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new InvalidConfigurationException(e);
        }

        objectByName.put(bean.getName(), ob);
        objectByClass.put(bean.getClassName(), ob);
    }

    @Override
    public String toString() {
        return "ObjectsByCLass: " + objectByClass.toString() +
                "\nObjectsByName: " + objectByName.toString();
    }
}
