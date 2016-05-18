package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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
    private Map<String, Object> objByName = new HashMap<>();
    private Map<String, Object> objByClassName = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */

    public Container(String pathToConfig) throws InvalidConfigurationException {

        BeanXmlReader reader = new BeanXmlReader();
        try {
            beans = reader.parseBeans(pathToConfig);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        BeanGraph graph = new BeanGraph();
        List<BeanVertex> vertices = new ArrayList<>();
        for (Bean b : beans) {
            vertices.add(graph.addVertex(b));
        }
        for (BeanVertex b : vertices) {
            for (Property p : b.getBean().getProperties().values()) {
                if (p.getType() == ValueType.REF) {
                    for (BeanVertex bn : vertices) {
                        if (bn.getBean().getName().equals(p.getValue())) {
                            graph.addEdge(bn, b);
                        }
                    }
                }
            }
        }

        List<BeanVertex> sorted = null;
        try {
            sorted = graph.sort();
        } catch (CycleReferenceException e) {
            e.printStackTrace();
        }

        for (BeanVertex b : sorted) {
            this.instantiateBean(b.getBean());
        }

    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
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

    private void instantiateBean(Bean bean) {
        try {
            String className = bean.getClassName();
            Class clazz = Class.forName(className);

            Object ob = clazz.newInstance();
            for (String name : bean.getProperties().keySet()) {
                try {
                    Field field = clazz.getDeclaredField(name);

                    field.setAccessible(true);
                    if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                        int temp = Integer.parseInt(bean.getProperties().get(name).getValue());
                        field.setInt(ob, temp);
                    } else {
                        field.set(ob, objByName.get(bean.getProperties().get(name).getValue()));
                    }
                } catch (NoSuchFieldException e) {
                    throw new InvalidConfigurationException("Нет такого поля");
                }
            }
            objByName.put(bean.getName(), ob);
            objByClassName.put(bean.getClassName(), ob);
            System.out.println(ob.toString() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
