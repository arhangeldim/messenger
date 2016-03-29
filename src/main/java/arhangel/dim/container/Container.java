package arhangel.dim.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Bean> beanById;
    private Map<String, Bean> beanByClass;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        String config = "";
        Scanner scanner = new Scanner(pathToConfig);
        while (scanner.hasNext()) {
            config += scanner.nextLine() + "\n";
        }
        beans = BeanXmlReader.read(config);
        for (Bean bean : beans) {
            beanById.put(bean.getName(), bean);
            beanByClass.put(bean.getClassName(), bean);
        }

        Map<String, BeanVertex> verticesByBeanNames = new HashMap<>();
        BeanGraph graph = new BeanGraph();

        for (Bean bean : beans) {
            for (Property property : bean.getProperties().values()) {
                if (property.getType() == ValueType.REF) {
                    graph.addEdge(verticesByBeanNames.get(bean.getName()),
                            verticesByBeanNames.get(property.getValue()));
                }
            }
        }

        try {
            List<BeanVertex> sortedGraph = graph.topSort();
            beans = new ArrayList<>();
            for (BeanVertex vertex : sortedGraph) {
                beans.add(vertex.getBean());
            }
        } catch (CycleReferenceException e) {
            throw new InvalidConfigurationException("Cycle in references");
        }

        for (Bean bean : beans) {
            instantiateBean(bean);
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        if (!beanById.containsKey(name)) {
            throw new UnsupportedOperationException(String
                    .format("No such bean: %s", name));
        }
        return beanById.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return beanByClass.get(className);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        String className = bean.getClassName();
        Class clazz;
        Object ob;
        try {
            clazz = Class.forName(className);
            ob = clazz.newInstance();
        } catch (Exception e) {
            throw new InvalidConfigurationException(String
                    .format("Unable to create instance of class %s", className));
        }

        for (String name : bean.getProperties().keySet()) {
            // ищем поле с таким именен внутри класса
            // учитывая приватные
            Field field;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                throw new InvalidConfigurationException("No such field " + name);
            }

            // Делаем приватные поля доступными
            field.setAccessible(true);

            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее

            Method method;
            String setterName = "set" + Character.toUpperCase(name.charAt(0)) +
                    name.substring(1);
            try {
                method = clazz.getDeclaredMethod(setterName, field.getType());
            } catch (NoSuchMethodException e) {
                throw new InvalidConfigurationException("No such method " + setterName);
            }

            Property prop = bean.getProperties().get(name);
            Type type = field.getType();

            try {
                switch (prop.getType()) {
                    case VAL:
                        method.invoke(ob, objectByClassName(type.getTypeName(), prop.getValue()));
                        break;
                    case REF:
                        String refName = prop.getValue();
                        method.invoke(ob, getByName(refName));
                        break;
                    default:
                }
            } catch (Exception e) {
                throw new InvalidConfigurationException(String
                        .format("Failed to instantiate field %s", name));
            }
        }
        beanById.put(bean.getName(), bean);
        beanByClass.put(bean.getClassName(), bean);
    }

    private Object objectByClassName(String className, String value) {
        if (Objects.equals(className, "int") || Objects.equals(className, "Integer")) {
            return Integer.parseInt(value);
        }
        if (Objects.equals(className, "String")) {
            return value;
        }
        throw new UnsupportedOperationException("convert to class " + className);
    }

}
