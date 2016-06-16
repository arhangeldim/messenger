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
        Object ob;
        try {
            Class clazz = Class.forName(className);
            ob = clazz.newInstance();
            for (String name : bean.getProperties().keySet()) {
                Property property = bean.getProperties().get(name);
                String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                if (property.getType() == ValueType.REF) {
                    Object ref = getByName(property.getValue());
                    Method method = clazz.getMethod(methodName, ref.getClass());
                    method.invoke(ob, ref);
                } else {
                    Method method = clazz.getMethod(methodName, int.class);
                    method.invoke(ob, Integer.parseInt(property.getValue()));
                }
            }
        } catch (InstantiationException e) {
            throw new InvalidConfigurationException("InstantiationException");
        } catch (InvocationTargetException e) {
            throw new InvalidConfigurationException("InvocationTargetException");
        } catch (NoSuchMethodException e) {
            throw new InvalidConfigurationException("NoSuchMethodException");
        } catch (IllegalAccessException e) {
            throw new InvalidConfigurationException("IllegalAccessException");
        } catch (ClassNotFoundException e) {
            System.out.println(bean.getClassName());
            throw new InvalidConfigurationException("ClassNotFoundException");
        }
        objByName.put(bean.getName(), ob);
        objByClassName.put(bean.getClassName(), ob);

    }
}
