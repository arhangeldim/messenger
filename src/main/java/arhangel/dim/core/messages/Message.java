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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Message message = (Message) other;
        return Objects.equals(id, message.id) &&
                Objects.equals(senderId, message.senderId) &&
                type == message.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senderId, type);
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
