package arhangel.dim.container;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Представляет тег bean из конфига
 */
@XmlRootElement(name = "bean")
public class Bean {

    private String name; // Уникально имя бина
    private String className; // Класс бина
    private List<Property> properties; // Набор параметров бина

    public Bean(String name, String className, List<Property> properties) {
        this.name = name;
        this.className = className;
        this.properties = properties;
    }

    public Bean() {}

    @XmlElementRef
    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @XmlAttribute(name = "id")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "class")
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

    @XmlRootElement(name = "root")
    public static class Beans {

        private List<Bean> beans;

        @XmlElementRef
        public List<Bean> getBeans() {
            return beans;
        }

        public void setBeans(List<Bean> beans) {
            this.beans = beans;
        }
    }

    // Бин определяется уникальным именем. Бины с одинаковыми именами являются одинаковыми

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Bean)) {
            return false;
        }

        Bean bean = (Bean) object;

        return name.equals(bean.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
