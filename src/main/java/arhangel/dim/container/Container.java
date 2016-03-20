package arhangel.dim.container;

import java.lang.reflect.Field;
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
        BeanXmlReader reader = new BeanXmlReader();
        try {
            this.beans = reader.parseBeans(pathToConfig); // вызываем BeanXmlReader
            for (Bean elem : this.beans) {
                instantiateBean(elem); //instance
            }
        } catch (Exception exc) {
            throw new InvalidConfigurationException(exc.getMessage());
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

    private void instantiateBean(Bean bean) throws InvalidConfigurationException {
        String className = bean.getClassName();
        try {
            Class clazz = Class.forName(className);
            Object obj = clazz.newInstance();

            for (String name : bean.getProperties().keySet()) {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);

                if (bean.getProperties().get(name).getType() == ValueType.VAL) {
                    field.set(obj, Integer.parseInt(bean.getProperties().get(name).getValue()));
                } else {
                    field.set(obj, objByName.get(bean.getProperties().get(name).getValue()));
                }
            }
            objByName.put(bean.getName(), obj);
            objByClassName.put(bean.getClassName(), obj);

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exc) {
            exc.printStackTrace();
        } catch (NoSuchFieldException exc) {
            throw new InvalidConfigurationException(exc.getMessage());
        }
        /*
        // Примерный ход работы

        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        // ищем дефолтный конструктор
        Object ob = clazz.newInstance();


        for (String name : bean.getProperties().keySet()) {
            // ищем поле с таким именен внутри класса
            // учитывая приватные
            Field field = clazz.getDeclaredField(name);
            // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки

            // Делаем приватные поля доступными
            field.setAccessible(true);

            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее

            */

    }

}
