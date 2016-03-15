package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<BeanVertex> beans;
    private Map<String, Object> objByName;
    private Map<String, Object> objByClassName;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        try {
            beans = (new BeanGraphBuilder()).buildFromXml(pathToConfig)
                    .getTopSort();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new InvalidConfigurationException(e);
        }

        objByName = new HashMap<>();
        objByClassName = new HashMap<>();
        instantiateBeans();
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
     * Например, Car car = (Car) container
     *                              .getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return objByClassName.get(className);
    }

    private void instantiateBeans() throws InvalidConfigurationException {
        for (BeanVertex beanVertex: beans) {
            Bean bean = beanVertex.getBean();
            String className = bean.getClassName();
            try {
                Class clazz = Class.forName(className);
                Object ob;
                ob = clazz.newInstance();
                for (String name: bean.getProperties().keySet()) {
                    Property property = bean.getProperties().get(name);

                    String methodName = "set" +
                            name.substring(0, 1).toUpperCase() +
                            name.substring(1);


                    if (property.getType() == ValueType.REF) {
                        Object parameter = getByName(property.getValue());
                        Method method = clazz.getMethod(methodName,
                                parameter.getClass());

                        method.invoke(ob, parameter);
                    } else {
                        Method method = clazz.getMethod(methodName,
                                int.class);
                        method.invoke(ob, new Integer(property.getValue()));
                    }
                    objByName.put(bean.getName(), ob);
                    objByClassName.put(bean.getClassName(), ob);
                }
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | ClassNotFoundException |
                    NoSuchMethodException e) {
                throw new InvalidConfigurationException(e);
            }

        }
    }

    public static void main(String[] args) {

    }
}
