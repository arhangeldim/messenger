package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by tatiana on 28.04.16.
 */
public class HistChatMessage extends Message {
    private Long chatId;

    public HistChatMessage() {
        this.setType(Type.MSG_CHAT_HIST);
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        HistChatMessage histChatMessage = (HistChatMessage) other;

        return Objects.equals(chatId, histChatMessage.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }

    @Override
    public String toString() {
        return "Chat list: " +
                chatId.toString();
    }
}
