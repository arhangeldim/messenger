package arhangel.dim.container;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private BeanGraph beans;
    private Map<String, Object> objectByName;
    private Map<String, Object> objectByClassName;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        beans = new BeanGraph();
        objectByName = new HashMap<>();
        objectByClassName = new HashMap<>();

        List<Bean> parsingResult = new ArrayList<>();
        try {
            parsingResult = BeanXmlReader.parseBeans(pathToConfig);
        } catch (IOException ex1) {
            throw new InvalidConfigurationException(ex1.getMessage());
        } catch (ClassNotFoundException ex2) {
            throw new InvalidConfigurationException(ex2.getMessage());
        }
        for (Bean bean: parsingResult) {
            beans.addVertex(bean);
        }
        List<Bean> sortedBeans = new ArrayList<>();
        beans.topSort()
                .stream()
                .forEach( bv->
                { sortedBeans.add(bv.getBean()); } );
        sortedBeans.stream().forEach(bean-> {
            try {
                instantiateBean(bean);
            } catch (InvalidConfigurationException ex) {
                System.out.print(ex.getMessage());
                return;
            }
        });

    }


    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {
        return objectByName.get(name);
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        return objectByClassName.get(className);
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        String className = bean.getClassName();
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException err) {
            System.err.println("incorrect name of class " + err.getMessage());
            return;
        }
        // ищем дефолтный конструктор

        Object obj;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException err) {
            System.err.println("can't initialize class " + err.getMessage());
            return;
        } catch (IllegalAccessException err) {
            System.err.println("illegal access " + err.getMessage());
            return;
        }

        for (Property pr : bean.getProperties().values()) {
            String methodName = "set" + pr.getName().substring(0,1).toUpperCase() + pr.getName().substring(1);
            Field field = null;
            try {
                field = clazz.getDeclaredField(pr.getName());
                field.setAccessible(true);
                if (pr.getType() == ValueType.VAL) {
                    field.set(obj, Integer.parseInt(pr.getValue().toString()));
                } else {
                    if (!objectByName.containsKey(pr.getValue())) {
                        throw new InvalidConfigurationException("invalid reference");
                    } else {
                        try {
                            Method method = clazz.getDeclaredMethod(methodName, getByName(pr.getValue()).getClass());
                            try {
                                method.invoke(obj, getByName(pr.getValue()));
                            } catch (InvocationTargetException e) {
                                throw new IllegalArgumentException(e.getMessage());
                            }
                        } catch (NoSuchMethodException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    }
                }
            } catch (NoSuchFieldException ex) {
                System.err.println("incorrect name of class " + ex.getMessage());
                return;
            } catch (IllegalAccessException ex) {
                throw new InvalidConfigurationException(ex.getMessage());
            }
            // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки

            // Делаем приватные поля доступными


            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
        }
        objectByClassName.put(bean.getClassName(), obj);
        objectByName.put(bean.getName(), obj);

    }

}

