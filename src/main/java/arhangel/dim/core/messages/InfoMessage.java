package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class InfoMessage extends Message {
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
        InfoMessage message = (InfoMessage) other;
        return Objects.equals(userId, message.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, userId);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "userId=" + userId +
                '}';
    }
}
