package arhangel.dim.container;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    Map<String, Object> objByClassName = new HashMap<>();

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        BeanXmlReader reader = new BeanXmlReader();
        beans = reader.parseBeans(pathToConfig);
        for (Bean bean : beans) {
            this.instantiateBean(bean);
        }
    }

    /**
     *  Вернуть объект по имени бина из конфига
     *  Например, Car car = (Car) container.getByName("carBean")
     */
    public Object getByName(String name) throws IllegalAccessException, ClassNotFoundException, InstantiationException {

        Object object = new Object();

        for (int i = 0; i < beans.size(); i++) {
            if (beans.get(i).getName().equals(name)) {
                String className = beans.get(i).getClassName();
                object = this.getByClass(className);
            }
        }
        return object;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clazz = Class.forName(className);
        return clazz.newInstance();
    }

    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        // Примерный ход работы

        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        // ищем дефолтный конструктор
        Object ob = clazz.newInstance();


        for (String name : bean.getProperties().keySet()) {
            try {
                // ищем поле с таким именен внутри класса
                // учитывая приватные

                Field field = clazz.getDeclaredField(name);
                // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки

                // Делаем приватные поля доступными
                field.setAccessible(true);

                if (bean.getProperties().get(name).getType().equals(ValueType.VAL)) {
                    switch (field.getType().getName()) {
                        case "int":
                            field.set(ob, Integer.parseInt(bean.getProperties().get(name).getValue()));
                            break;
                        default:
                            break;
                    }
                }
                else if (bean.getProperties().get(name).getType().equals(ValueType.REF)) {
                    Object objToRef = objByClassName.get(bean.getProperties().get(name).getValue());
                    field.set(ob, objToRef);
                }


                // Далее определяем тип поля и заполняем его
                // Если поле - примитив, то все просто
                // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
            }
            catch (NoSuchFieldException e) {
                e.getMessage();
            }
        }
        objByClassName.put(bean.getName(), ob);
    }

}
