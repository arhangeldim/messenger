package arhangel.dim.container;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

    private void function() throws InvalidConfigurationException, CycleReferenceException {
        List<Integer> prevGen = new ArrayList();
        List<Integer> curGen = new ArrayList();

        prevGen.add(1);
    }

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException, IllegalAccessException, InstantiationException, ClassNotFoundException, CycleReferenceException {

        BeanXmlReader beanReader = new BeanXmlReader();
        BeanGraph beanGraph = new BeanGraph();
        beans = beanGraph.sortedBeanList(beanReader.parseBeans(pathToConfig));

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
                        case "java.lang.String":
                            field.set(ob, bean.getProperties().get(name).getValue());
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
        objByClassName.put(clazz.getName(), ob);
        objByName.put(bean.getName(), ob);
    }

//    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvalidConfigurationException {
//
//
//        // Примерный ход работы
//
//        try {
//
//            String className = bean.getClassName();
//            Class clazz = Class.forName(className);
//            // ищем дефолтный конструктор
//            Object ob = clazz.newInstance();
//
//            for (String name : bean.getProperties().keySet()) {
//                // ищем поле с таким именен внутри класса
//                // учитывая приватные
//                Field field = clazz.getDeclaredField(name);
//                // проверяем, если такого поля нет, то кидаем InvalidConfigurationException с описание ошибки
//
//                // Делаем приватные поля доступными
//                field.setAccessible(true);
//
//                Method method = clazz.getDeclaredMethod("set" + name, );
//
//                // Далее определяем тип поля и заполняем его
//                // Если поле - примитив, то все просто
//                // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
//
//                ValueType type = bean.getProperties().get(name).getType();
//                if (type == ValueType.VAL) {
//
//                    String fieldType = field.getType().getSimpleName();
//                    switch (fieldType) {
//                        case "Integer": field.set(ob, Integer.valueOf(bean.getProperties().get(name).getValue()));
//                            break;
//                        case "String": field.set(ob, bean.getProperties().get(name).toString());
//                            break;
//                    }
//
//                } else {
//
//                    if (objByName.get(bean.getProperties().get(name).getValue()) == null) {
//                        System.out.println("null");
//                    }
//                    field.set(ob, objByName.get(bean.getProperties().get(name).getValue()));
//                }
//
//
//            }
//
//            objByName.put(bean.getName(), ob);
//            objByClassName.put(bean.getClassName(), ob);
//        } catch (NoSuchFieldException e) {
//            throw new InvalidConfigurationException("config.xml error: requested class does not exist");
//        }
//
//
//    }

}
