package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by dmitriy on 08.05.16.
 */
public class InfoMessage extends Message {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
    public String toString() {
        return "InfoMessage{" +
                "userId='" + userId + '\'' +
                '}';
    }
}
