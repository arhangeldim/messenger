package arhangel.dim.container;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private Map<String, Object> objectsByName;
    private Map<String, Object> objectsByClass;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        Map<String, Bean> beans = new BeanXmlReader().parseBeans(pathToConfig);
        Map<String, BeanVertex> vertices = new HashMap<>(beans.size());

        BeanGraph graph = new BeanGraph();

        // fill graph with vertices and edges
        beans.forEach((beanName, bean) -> {
            vertices.put(beanName, graph.addVertex(bean));
        });

        vertices.forEach((beanName, beanVertex) -> {
            beanVertex.getBean().getProperties().forEach(property -> {
                if (property.getRef() != null) {
                    graph.addEdge(beanVertex,vertices.get(property.getRef()));
                }
            });
        });

        List<Bean> orderedBeans;
        try {
            orderedBeans = graph.getOrderedBeans();
        } catch (CycleReferenceException e) {
            throw new InvalidConfigurationException(e);
        }

        objectsByClass = new HashMap<>(orderedBeans.size());
        objectsByName = new HashMap<>(orderedBeans.size());

        for (Bean bean: orderedBeans) {
            try {
                Object object = instantiateBean(bean);
                objectsByName.put(bean.getName(), object);
                objectsByClass.put(bean.getClassName(), object);
            } catch (Exception e) {
                throw new InvalidConfigurationException(e);
            }
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

    private Object instantiateBean(Bean bean) throws Exception {
        Class clazz = Class.forName(bean.getClassName());

        Object instance = clazz.newInstance();

        Map<String, Method> setters = new HashMap<>();

        for (Method method: clazz.getMethods()) {
            if (method.getName().startsWith("set")) {
                setters.put(method.getName(),method);
            }
        }

        for (Property property: bean.getProperties()) {
            String ref = property.getRef();
            String name = property.getName();
            if (ref != null) {
                // should search setters with non primitive type
                Method method = clazz.getMethod(getSetterName(name),objectsByName.get(ref).getClass());
                if (method == null) {
                    throw new InvalidConfigurationException("Can't get setter for field " + name);
                }
                method.invoke(instance, objectsByName.get(ref));
            } else {
                // set primitive value
                Method setter = setters.get(getSetterName(name));
                Class[] parameters = setter.getParameterTypes();
                if (parameters.length != 1) {
                    throw new InvalidConfigurationException("Setter for field " + name + " doesn't have one parameter");
                }

                // we should get object of type parameters[0] from our value of String type
                // All wrappers have method called "valueOf(String.class),
                // so we don't care about what primitive wrapper is in the setter
                Object parameter = parameters[0].getMethod("valueOf", String.class).invoke(null, property.getVal());
                // this will work only with wrappers!

                setter.invoke(instance, parameter);

            }
        }
        return instance;
    }

    public String getSetterName(String fieldName) {
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }


    public static void main(String[] args) throws InvalidConfigurationException {
        Container container = new Container("config.xml");
        System.out.println(container.getByClass("arhangel.dim.container.beans.Car"));
        System.out.println(container.getByName("carBean"));
        System.out.println(container.getByName("engineBean"));
        System.out.println(container.getByName("gearBean"));
    }

}
