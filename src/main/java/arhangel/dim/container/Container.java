package arhangel.dim.container;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Object> objByName = new HashMap<>();
    private Map<String, Object> objByClassName = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, CycleReferenceException {
        beans = new BeanGraph(new BeanXmlReader().parseBeans(pathToConfig)).getSortedBeans();
        for (Bean bean: beans) {
            instantiateBean(bean);
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
        return objByClassName.get(className);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        String className = bean.getClassName();
        Object ob = null;
        try {
            Class clazz = Class.forName(className);
            ob = clazz.newInstance();
            for (String name : bean.getProperties().keySet()) {
                Property property = bean.getProperties().get(name);
                String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method setter = null;
                for (Method method: clazz.getDeclaredMethods()) {
                    if (method.getName().equals(methodName)) {
                        setter = method;
                    }
                }
                if (property.getType() == ValueType.REF) {
                    Object ref = getByName(property.getValue());
                    setter.invoke(ob, ref);
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
                        case "java.lang.String":
                            setter.invoke(ob, value);
                            break;
                        default:
                            throw new Exception("cannot set the field " + field.toString());
                    };
                }
            }
        } catch (InstantiationException e) {
            throw new InvalidConfigurationException("InstantiationException");
        } catch (InvocationTargetException e) {
            throw new InvalidConfigurationException("InvocationTargetException");
        } catch (IllegalAccessException e) {
            throw new InvalidConfigurationException("IllegalAccessException");
        } catch (ClassNotFoundException e) {
            System.out.println(bean.getClassName());
            throw new InvalidConfigurationException("ClassNotFoundException");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO refactor catches
        objByName.put(bean.getName(), ob);
        objByClassName.put(bean.getClassName(), ob);

    }
}
