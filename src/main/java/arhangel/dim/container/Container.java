package arhangel.dim.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.reflect.Field;

public class Container {
    private List<Bean> beans;

    private Map<String, Object> objByName = new HashMap<>();
    private Map<String, Object> objByClass = new HashMap<>();

    public Container(String pathToConfig) throws InvalidConfigurationException {
        BeanXmlReader beanRead = new BeanXmlReader();
        try {
            beans = beanRead.parseBeans(pathToConfig);
        } catch (Exception e) {
            throw new InvalidConfigurationException("Configuration exception");
        }
        for (Bean bean : beans) {
            try {
                instantiateBean(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object getByName(String name) {
        for (Map.Entry<String, Object> entry : objByName.entrySet()) {
            String key = entry.getKey();
            if (key.equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Object getByClass(String className) {
        for (Map.Entry<String, Object> entry : objByClass.entrySet()) {
            String key = entry.getKey();
            if (key.equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void instantiateBean(Bean bean) throws Exception {

        String className = bean.getClassName();
        Class clazz = Class.forName(className);
        Object ob = clazz.newInstance();

        for (String name : bean.getProperties().keySet()) {
            Field field = clazz.getDeclaredField(name);

            if (field == null) {
                throw new InvalidConfigurationException("There is no such field");
            }

            field.setAccessible(true);

            ValueType type = bean.getProperties().get(name).getType();
            switch (type) {
                case VAL:
                    field.setInt(ob, new Integer(bean.getProperties().get(name).getValue()));
                    break;
                case REF:
                    field.set(ob, getByName(bean.getProperties().get(name).getValue()));
                    break;
                default:
                    break;
            }
        }
        objByName.put(bean.getName(), ob);
        objByClass.put(className, ob);
    }
}
