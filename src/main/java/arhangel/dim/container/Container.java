package arhangel.dim.container;

import arhangel.dim.container.beans.Car;
import arhangel.dim.container.beans.Engine;
import arhangel.dim.container.beans.Gear;
import arhangel.dim.container.exceptions.InvalidConfigurationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {

        try {
            BeanXmlReader beanXmlReader = new BeanXmlReader();
            beans = beanXmlReader.parseBeans(pathToConfig);
            for (Bean bean: beans) {
                instantiateBean(bean);
            }
        } catch (InvalidConfigurationException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
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

    public List<Bean> getBeans() {
        return beans;
    }

    // метод, кастующий строку аргумента value к его типу clazz
    private Object parseValue(String clazz, String value) {
        switch (clazz) {
            case "boolean":
                return Boolean.valueOf(value);
            case "byte":
                return Byte.valueOf(value);
            case "short":
                return Short.valueOf(value);
            case "int":
                return Integer.valueOf(value);
            case "long":
                return Long.valueOf(value);
            case "double":
                return Double.valueOf(value);
            case "float":
                return Float.valueOf(value);
            case "java.lang.String":
                return value;
            default:
                return objByName.get(value);
        }
    }

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        try {
            String className = bean.getClassName();
            Class clazz = Class.forName(className);
            // ищем дефолтный конструктор
            Object object = clazz.newInstance();

            for (String name : bean.getProperties().keySet()) {
                // ищем поле с таким именен внутри класса
                // учитывая приватные
                Field field = clazz.getDeclaredField(name);
                // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описанием ошибки

                // определяем тип аргумента для передачи методу set
                Class[] argTypes = new Class[] { field.getType() };
                // определяем имя сеттера
                String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                // по имени и типу аргумента получаем метод
                Method setFieldValue = clazz.getDeclaredMethod(methodName, argTypes);

                // определем value to be set
                Object valueToSet = parseValue(field.getType().getName(), bean.getProperties().get(name).getValue());
                // вызываем setter
                setFieldValue.invoke(object, valueToSet);

            }

            objByName.put(bean.getName(), object);
            objByClassName.put(className, object);

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidConfigurationException(e.getMessage());
        }
    }

}
