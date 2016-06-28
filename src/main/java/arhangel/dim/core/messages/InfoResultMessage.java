package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class InfoResultMessage extends Message {
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        InfoResultMessage message = (InfoResultMessage) other;
        return Objects.equals(info, message.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), info);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "info='" + info + '\'' +
                '}';
    }
}
