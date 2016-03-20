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
//        вызываем BeanXmlReader
        BeanXmlReader reader = new BeanXmlReader();
        try {
            beans = reader.parseBeans(pathToConfig);
        } catch (Exception e) {
            throw new InvalidConfigurationException("wrong config");
        }

        for (Bean bean : beans) {
            try {
                instantiateBean(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) {

        for (Map.Entry<String, Object> entry: objByName.entrySet()) {
            String key = entry.getKey();
            if (key.equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) {
        for (Map.Entry<String, Object> entry: objByClassName.entrySet()) {
            String key = entry.getKey();
            if (key.equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, NoSuchFieldException, InvalidConfigurationException {


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
            if (field == null) throw new InvalidConfigurationException("doesn't exist class");
            // Делаем приватные поля доступными
            field.setAccessible(true);

            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
            ValueType valueType = bean.getProperties().get(name).getType();
            switch (valueType) {
                case VAL:
                    field.setInt(ob, new Integer(bean.getProperties().get(name).getValue()));
                    break;
                case REF:
                    field.set(ob, getByName(bean.getProperties().get(name).getValue()));
                    break;
            }

        }

        objByName.put(bean.getName(), ob);
        objByClassName.put(className, ob);

    }

}
