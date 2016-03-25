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
    public Map<String, Object> objByName = new HashMap<>();
    public Map<String, Object> objByClassName = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        BeanXmlReader beanReader = new BeanXmlReader();
        BeanGraph beanGraph = new BeanGraph();
        beans = beanGraph.sortedBeanList(beanReader.parseBeans("config.xml"));

        for (int b = 0; b < beans.size(); b++) {
            instantiateBean(beans.get(b));
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

    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidConfigurationException {


        // Примерный ход работы

        try {

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

                ValueType type = bean.getProperties().get(name).getType();
                if (type == ValueType.VAL) {
                    System.out.println(Integer.valueOf(bean.getProperties().get(name).getValue()).toString());
                    field.set(ob, Integer.valueOf(bean.getProperties().get(name).getValue()));
                } else {
                    if (objByName.get(bean.getProperties().get(name).getValue()) == null) {
                        System.out.println("null");
                    }
                    field.set(ob, objByName.get(bean.getProperties().get(name).getValue()));
                }


            }

            objByName.put(bean.getName(), ob);
            objByClassName.put(bean.getClassName(), ob);
        } catch (NoSuchFieldException e) {
            throw new InvalidConfigurationException("config.xml error: requested class does not exist");
        }


    }

}
