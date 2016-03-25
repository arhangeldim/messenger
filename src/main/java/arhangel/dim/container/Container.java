package arhangel.dim.container;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Используйте ваш xml reader чтобы прочитать конфиг и получить список бинов
 */
public class Container {
    private List<Bean> beans;
    private Map<String, Object> objByClassName;
    private Map<String, Object> objByName;

    /**
     * Если не получается считать конфиг, то бросьте исключение
     * //* @throws InvalidConfigurationException неверный конфиг
     */
    public Container(String pathToConfig) throws InvalidConfigurationException {
        beans = new ArrayList<>();
        BeanXmlReader beanParser = new BeanXmlReader();
        try {
            List<BeanVertex> vertices = new ArrayList<>();
            BeanGraph graph = new BeanGraph();
            List<Bean> beanList = (new BeanXmlReader()).parseBeans("test.xml");
            for (Bean bean : beanList) {
                vertices.add(graph.addVertex(bean));
            }
            for (BeanVertex parentVertex : vertices) {
                for (BeanVertex childVertex : vertices) {
                    if (!parentVertex.equals(childVertex) && graph.isConnected(parentVertex, childVertex)) {
                        graph.addEdge(parentVertex, childVertex);
                    }
                }
            }
            List<BeanVertex> list = graph.sort();
            for (BeanVertex beanVertex : list) {
                Bean beanElement = beanVertex.getBean();
                this.beans.add(beanElement);
                instantiateBean(beanElement);
            }
        } catch (Exception e) {
            throw new InvalidConfigurationException("неверный конфиг");
        }
    }

    /**
     * Вернуть объект по имени бина из конфига
     * Например, Car car = (Car) container.getByName("carBean")
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

    private void instantiateBean(Bean bean) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {

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

            if (bean.getProperties().get(name).getType() == ValueType.REF) {
                field.set(name, getByName(bean.getProperties().get(name).getValue()));
            }
            else {
                field.set(name, bean.getProperties().get(name).getValue());
            }
            // Далее определяем тип поля и заполняем его
            // Если поле - примитив, то все просто
            // Если поле ссылка, то эта ссылка должа была быть инициализирована ранее
        }

        objByName.put(bean.getName(), ob);
        objByClassName.put(bean.getClassName(), ob);

    }
}
