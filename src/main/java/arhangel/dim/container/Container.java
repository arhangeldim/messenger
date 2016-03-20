package arhangel.dim.container;

import arhangel.dim.container.beans.Car;
import arhangel.dim.container.beans.Engine;
import arhangel.dim.container.beans.Gear;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {


    public  static void main(String[] args) {
        try {
            Container container = new Container("config.xml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private  Map<String, Object> objByName = new HashMap<>();
    private  Map<String, Object> objByClassName = new HashMap<>();

    private Map<String, Bean> beanNames = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        List<Bean> parsedBeans;
        try {
            BeanXmlReader xmlReader = new BeanXmlReader();
            parsedBeans = xmlReader.parseBeans(pathToConfig);
            instantiateBeans(parsedBeans);
        } catch (InvalidConfigurationException e) {
            throw e;
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
        return objByName.get(className);
    }

    private void instantiateBeans(List<Bean> beans) throws InvalidConfigurationException {
        GraphBuilder grBuilder = new GraphBuilder();
        BeanGraph gr = grBuilder.buildGraph(beans);
        List<BeanVertex> sortedBeanVertices = gr.sort();
        for (BeanVertex beanVertex: sortedBeanVertices) {
            try {
                instantiateBean(beanVertex.getBean());
            } catch (InvalidConfigurationException | IllegalArgumentException e) {
                throw new InvalidConfigurationException(e.getMessage());
            }
        }
    }

    private void instantiateBean(Bean bean)   throws IllegalArgumentException, InvalidConfigurationException {
        String className = bean.getClassName();
        String beanName = bean.getName();
        try {
            Class clazz = Class.forName(className);
            try {
                Object ob = clazz.newInstance();
                for (String name : bean.getProperties().keySet()) {
                    try {
                        Field field = clazz.getDeclaredField(name);
                        field.setAccessible(true);
                        if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                            Object value = bean.getProperties().get(name).getValue();
                            field.set(ob, Integer.parseInt(value.toString()));
                        } else {
                            if (!objByName.containsKey(bean.getProperties().get(name).getValue())) {
                                throw new InvalidConfigurationException("invalid reference");
                            } else {
                                Object value = getByName(bean.getProperties().get(name).getValue());
                                field.set(ob, value);
                            }
                        }
                    } catch (NoSuchFieldException e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                objByName.put(beanName, ob);
                objByClassName.put(className, ob);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

}
