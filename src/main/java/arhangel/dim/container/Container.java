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
        Object obj = null;
        try {
            obj = objByClassName.get(name);
        } catch (Exception e) {
            e.getMessage();
        }
        return obj;
    }

    /**
     * Вернуть объект по имени класса
     * Например, Car car = (Car) container.getByClass("arhangel.dim.container.Car")
     */
    public Object getByClass(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object object = null;
        for (Object obj : objByClassName.values()) {
            if (obj.getClass().toString().replace("class ", "").equals(className)) {
                object = obj;
            }
        }
        return object;
    }

    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        // ищем дефолтный конструктор
        Object ob = clazz.newInstance();

        for (String name : bean.getProperties().keySet()) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);

                if (bean.getProperties().get(name).getType()
                        .equals(ValueType.VAL)) {                       // Если поле - значение
                    switch (field.getType().getName()) {
                        case "int":
                            field.set(ob, Integer.parseInt(bean.getProperties().get(name).getValue()));
                            break;
                        case "float":
                            field.set(ob, Float.parseFloat(bean.getProperties().get(name).getValue()));
                            break;
                        default:
                            break;
                    }
                } else if (bean.getProperties().get(name).getType()
                        .equals(ValueType.REF)) {                // Если поле - ссылка
                    Object objToRef = objByClassName.get(bean.getProperties().get(name).getValue());
                    field.set(ob, objToRef);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
        objByClassName.put(bean.getName(), ob);
    }

}
