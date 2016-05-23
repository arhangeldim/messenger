package arhangel.dim.core.messages;

import java.io.Serializable;
import java.util.Objects;

/**
 * Базовый класс для всех сообщений
 */
public abstract class Message implements Serializable {
    private Long senderId;
    private Type type;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", type=" + type +
                '}';
    }
}
