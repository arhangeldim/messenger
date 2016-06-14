package arhangel.dim.container;

import arhangel.dim.container.dag.Graph;
import arhangel.dim.container.dag.Vertex;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.StringJoiner;


/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Object> objByName;
    private Map<String, Object> objByClass;

    public Container(String pathToConfig) throws InvalidConfigurationException {

        BeanXmlReader reader = new BeanXmlReader();

        List<Bean> beanList = reader.parseBeans(pathToConfig);
        List<Vertex<Bean>> vertexList = new ArrayList<>();
        Graph<Bean> graph = new Graph<>();
        Map<String, Vertex<Bean>> vertexById = new TreeMap<>();

        for ( int i = 0; i < beanList.size(); i++ ) {
            Bean bean = beanList.get(i);
            Vertex<Bean> vertex = graph.addVertex(bean);
            vertexById.put(bean.getName(), vertex);
            vertexList.add(vertex);
        }

        for ( int i = 0; i < vertexList.size(); i++ ) {
            Vertex<Bean> vertex = vertexList.get(i);
            Map<String, Property> mp = vertex.getValue().getProperties();
            for ( Map.Entry<String, Property> entry : mp.entrySet()) {
                if ( entry.getValue().getType() == ValueType.REF ) {
                    String ref = entry.getValue().getValue();
                    System.out.print(ref + "\n");
                    graph.addEdge(vertex, vertexById.get(ref), true);
                }
            }
        }
        vertexList = graph.toposort();
        beans = new LinkedList<>();
        for ( int i = 0; i < vertexList.size(); i++ ) {
            Bean bean = vertexList.get(i).getValue();
            System.out.print("\nObject: \n" + bean.toString() + "\n\n");
            beans.add(bean);
        }
        objByClass = new TreeMap<>();
        objByName = new TreeMap<>();
        try {
            if (graph.hasCycle()) {
                throw new CycleReferenceException("Here is a cicle");
            } else {
                for (Bean b : beans) {
                    preInstantiateBean(b);
                }
                for (Bean b : beans) {
                    instantiateBean(b);
                }
            }
        } catch ( CycleReferenceException e) {
            System.out.print(e.toString());
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

    public String toString() {
        StringJoiner str = new StringJoiner("\n");
        for (Map.Entry e : objByName.entrySet()) {
            str.add("\nObject:" + e.getKey() + " \n" + e.getValue());
        }
        return str.toString();
    }

    private Object fromString(String typeName, String data) throws Exception {
        switch (typeName) {
            case "double":
            case "Double":
                return Double.valueOf(data);
            case "int":
            case "Integer":
                return Integer.valueOf(data);
            case "boolean":
            case "Boolean":
                return Boolean.valueOf(data);
            default:
                throw new InvalidConfigurationException("type name = " + typeName);
        }
    }

    private void preInstantiateBean(Bean bean) {
        try {
            String className = bean.getClassName();
            Class clazz = Class.forName(className);
            Object ob = clazz.newInstance();
            objByName.put(bean.getName(), ob);
            objByClass.put(bean.getClassName(), ob);
        } catch (ClassNotFoundException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (InstantiationException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (Exception e) {
            System.out.print(e.getLocalizedMessage());
        }
    }

    private void instantiateBean(Bean bean) {

        try {
            String beanName = bean.getName();
            Object ob = getByName(beanName);
            Class clazz = ob.getClass();
            String className = bean.getClassName();
            for (String name : bean.getProperties().keySet()) {
                Field field = clazz.getDeclaredField(name);
                if (field == null) {
                    throw new InvalidConfigurationException("Failed to set field [" + name + "] for class " + clazz.getName());
                }
                field.setAccessible(true);
                Property prop = bean.getProperties().get(name);
                if (prop.getType() == ValueType.VAL) {
                    field.set(ob, fromString(field.getType().getTypeName(), prop.getValue()));
                } else if (prop.getType() == ValueType.REF) {
                    if (objByName.containsKey(prop.getValue())) {
                        field.set(ob, getByName(prop.getValue()));
                    } else {
                        throw new InvalidConfigurationException("Failed to instantiate bean. Field " +
                                name + ", object: " + className + "\n");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (InstantiationException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (NoSuchFieldException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            System.out.print(e.getLocalizedMessage());
        } catch (Exception e) {
            System.out.print(e.getLocalizedMessage());
        }

    }

}
