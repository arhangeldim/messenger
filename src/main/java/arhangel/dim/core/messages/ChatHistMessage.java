package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by dmitriy on 08.05.16.
 */
public class ChatHistMessage extends Message {
    private Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public ChatHistMessage(Long chatId) {
        this.chatId = chatId;
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
        ChatHistMessage message = (ChatHistMessage) other;
        return Objects.equals(chatId, message.chatId);
    }

    @Override
    public String toString() {
        return "ChatHistMessage{" +
                "chatId='" + chatId + '\'' +
                '}';
    }
}
