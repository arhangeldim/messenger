package arhangel.dim.core.messages;

import java.util.List;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class ChatHistoryResultMessage extends Message {
    private List<TextMessage> messagesText;

    public List<TextMessage> getMessagesText() {
        return messagesText;
    }

    public void setMessagesText(List<TextMessage> messagesText) {
        this.messagesText = messagesText;
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
        ChatHistoryResultMessage message = (ChatHistoryResultMessage) other;
        return Objects.equals(messagesText, message.messagesText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messagesText, messagesText);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "messagesText=" + messagesText +
                '}';
    }
}
