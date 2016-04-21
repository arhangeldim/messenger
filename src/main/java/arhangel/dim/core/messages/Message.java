package arhangel.dim.core.messages;

import java.io.Serializable;
import java.util.Objects;

/**
 * Базовый класс для всех сообщений
 */
public abstract class Message implements Serializable {

    private Long id;
    private Long senderId;
    private Type type;
    private Long timestamp;

    public Message() {
    }

    public Message(Type type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Message message = (Message) object;

        if (id != null ? !id.equals(message.id) : message.id != null) {
            return false;
        }
        if (senderId != null ? !senderId.equals(message.senderId) : message.senderId != null) {
            return false;
        }
        if (type != message.type) {
            return false;
        }
        return !(timestamp != null ? !timestamp.equals(message.timestamp) : message.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", type=" + type +
                '}';
    }
}
