package ivanov.mikhail.container;

/**
 * Описание тега property в xml конфигурации.
 * Тег описывает поля определенного бина
 */
public class Property {
    private String name; // Имя поля
    private String value; // Значение поля
    private ValueType type; // Метка ссылочное значение или примитив

    public Property(String name, String value, ValueType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}
