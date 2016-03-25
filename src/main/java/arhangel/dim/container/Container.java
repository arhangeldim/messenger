package arhangel.dim.container;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private Map<String, Bean> beans = new HashMap<>();
    private Map<Bean, Object> instantiatedBeans = new HashMap<>();
    private Map<String, Bean> beanByClass = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        BeanXmlReader reader = new BeanXmlReader();
        List<Bean> beansList;
        try {
            beansList = reader.parseBeans(pathToConfig);
        } catch (IOException e) {
            // Tests contain catches for InvalidConfigurationException only -- why?
            // I really should not wrap this
            throw new InvalidConfigurationException("IOException: " + e.getMessage());
        }

        for (Bean bean: beansList) {
            if (beans.put(bean.getName(), bean) != null) {
                throw new InvalidConfigurationException("Duplicate bean name '" + bean.getName() + "'.");
            }

            beanByClass.put(bean.getClassName(), bean);
        }

        try {
            instantiateBeans();
        } catch (CycleReferenceException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        return instantiatedBeans.get(beans.get(name));
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     *
     * Если объектов такого класса несколько, возвращает произвольный.
     */
    public Object getByClass(String className) {
        return instantiatedBeans.get(beanByClass.get(className));
    }

    private void instantiateBeans() throws CycleReferenceException, InvalidConfigurationException {
        BeanGraph graph = new BeanGraph();

        HashMap<Bean, BeanVertex> beanVertexMap = new HashMap<>();
        for (Bean bean: beans.values()) {
            beanVertexMap.put(bean, graph.addVertex(bean));
        }

        for (Bean bean: beans.values()) {
            for (Property p : bean.getProperties().values()) {
                if (p.getType() == ValueType.REF) {
                    Bean refBean = beans.get(p.getValue());
                    if (refBean == null) {
                        throw new InvalidConfigurationException("Unresolved reference to '" + p.getValue() +
                                "' of bean '" + bean.getName() + "'.");
                    }

                    graph.addEdge(beanVertexMap.get(bean), beanVertexMap.get(refBean));
                }
            }
        }

        List<BeanVertex> sorted = graph.sortBeans();
        for (BeanVertex b: sorted) {
            instantiateBean(b.getBean());
        }
    }

    private String makeFirstUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private Object objectFromString(String str, Class desiredType) throws InvalidConfigurationException {
        if (desiredType == int.class) {
            return Integer.parseInt(str);
        }

        if (desiredType == String.class) {
            return str;
        }

        throw new InvalidConfigurationException("Creation of '" + desiredType.getName() +
                "' objects from string is not implemented.");
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        Class beanClass;

        try {
            beanClass = Class.forName(bean.getClassName());
        } catch (ClassNotFoundException e) {
            throw new InvalidConfigurationException("Class '" + bean.getClassName() +
                    "' of bean '" + bean.getName() + "' not found.");
        }

        try {
            Object beanInstance = beanClass.newInstance();

            for (String propertyName: bean.getProperties().keySet()) {
                Property property = bean.getProperties().get(propertyName);
                String setterName = "set" + makeFirstUpper(propertyName);

                // search by hand because parameters are unknown
                Method[] methods = beanClass.getMethods();
                Method found = null;
                for (Method m: methods) {
                    if (m.getName().equals(setterName)) {
                        if (found != null) {
                            throw new InvalidConfigurationException("Multiple setters found in '" +
                                    bean.getClassName() + "' for '" + propertyName + "'.");
                        }
                        found = m;
                    }
                }

                if (found == null) {
                    throw new InvalidConfigurationException("No setter '" + setterName +
                            "' found in '" + bean.getClassName() + "'.");
                }

                switch (property.getType()) {
                    case REF: {
                        found.invoke(beanInstance, getByName(property.getValue()));
                        break;
                    }

                    case VAL: {
                        Parameter[] params = found.getParameters();
                        if (params.length != 1) {
                            throw new InvalidConfigurationException("Setter for '" + propertyName + "' in '" +
                                    bean.getClassName() + "' must accept single parameter.");
                        }

                        Parameter param = params[0];
                        Object value = objectFromString(property.getValue(), param.getType());
                        found.invoke(beanInstance, value);
                        break;
                    }

                    default: assert false;
                }
            }

            instantiatedBeans.put(bean, beanInstance);
        } catch (Exception e) {
            throw new InvalidConfigurationException("Failed to instantiate bean: " + e.getMessage());
        }
    }

}
