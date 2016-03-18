package arhangel.dim.container;

import java.util.Map;

/**
 * Представляет тег bean из конфига
 */
public class Bean {
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Bean bean = (Bean) other;

        return name.equals(bean.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private String name; // Уникально имя бина
    private String className; // Класс бина
    private Map<String, Property> properties; // Набор полей бина ИмяПоля-Значение

    public Bean(String name, String className, Map<String, Property> properties) {
        this.name = name;
        this.className = className;
        this.properties = properties;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", properties=" + properties +
                '}';
    }
}
