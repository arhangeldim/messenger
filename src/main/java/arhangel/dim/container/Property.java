package arhangel.dim.container;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Описание тега property в xml конфигурации.
 * Тег описывает поля определенного бина
 */
@XmlRootElement
public class Property {
    private String name; // Имя поля
    private String val; // Примитивное значение
    private String ref; // ссылочное значение

    public Property(String name, String value, ValueType type) {
        this.name = name;
        if (ValueType.REF.equals(type)) {
            ref = value;
        } else {
            val = value;
        }
    }

    public Property() {}

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @XmlAttribute
    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @XmlTransient
    public String getValue() {
        return ref == null ? val : ref;
    }

    @XmlTransient
    public ValueType getType() {
        return ref == null ? ValueType.VAL : ValueType.REF;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + getName() + '\'' +
                ", value='" + getValue() + '\'' +
                ", type=" + getType() +
                '}';
    }
}
