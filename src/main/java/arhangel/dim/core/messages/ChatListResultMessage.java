package arhangel.dim.core.messages;

import java.util.List;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class ChatListResultMessage extends Message {
    private List<Long> chatIds;

    public List<Long> getChatIds() {
        return chatIds;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
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
        ChatListResultMessage message = (ChatListResultMessage) other;
        return Objects.equals(chatIds, message.chatIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chatIds, chatIds);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "chatIds=" + chatIds +
                '}';
    }
}
