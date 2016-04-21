package arhangel.dim.container;

import arhangel.dim.container.dag.Graph;
import arhangel.dim.container.dag.Vertex;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static jdk.nashorn.internal.runtime.JSType.isNumber;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    //private List<Bean> beans;
    private Map<String, Object> objByName = new HashMap<String, Object>();
    private Map<String, Object> objByClassName = new HashMap<String, Object>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     *
     * @throws InvalidConfigurationException неверный конфиг
     */

    public Container(String pathToConfig) throws InvalidConfigurationException {
        BeanXmlReader reader = new BeanXmlReader();
        List<Bean> beans = reader.parseBeans(pathToConfig);
        Graph<Bean> graph = new Graph<>();
        List<Vertex> vertices = new ArrayList<>();
        for (Bean b : beans) {
            vertices.add(graph.addVertex(b));
        }
        for (Vertex<Bean> b : vertices) {
            for (Property p : b.getValue().getProperties().values()) {
                if (p.getType() == ValueType.REF) {
                    for (Vertex<Bean> bn : vertices) {
                        if (bn.getValue().getName().equals(p.getValue())) {
                            graph.addEdge(bn, b, true);
                        }
                    }
                }
            }
        }
        List<Vertex<Bean>> sorted = graph.toposort();
        for (Vertex<Bean> b : sorted) {
            //this.beans.add(b.getValue());
            this.instantiateBean(b.getValue());
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

        // Примерный ход работы
        try {
            String className = bean.getClassName();
            Class clazz = Class.forName(className);
            // ищем дефолтный конструктор
            Object ob = clazz.newInstance();
            for (String name : bean.getProperties().keySet()) {
                // ищем поле с таким именен внутри класса
                // учитывая приватные
                try {
                    Field field = clazz.getDeclaredField(name);
                    // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки
                    // Делаем приватные поля доступными
                    field.setAccessible(true);
                    if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                        String str = bean.getProperties().get(name).getValue();
                        if (isNumber(str)) {
                            int temp = Integer.parseInt(str);
                            field.setInt(ob, temp);
                        } else {
                            field.set(ob, str);
                        }


                    } else {
                        field.set(ob, objByName.get(bean.getProperties().get(name).getValue()));
                    }
                } catch (NoSuchFieldException e) {
                    throw new InvalidConfigurationException("Нет такого поля");
                }
            }
            objByName.put(bean.getName(), ob);
            objByClassName.put(bean.getClassName(), ob);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
