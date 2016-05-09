package arhangel.dim.core.messages;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created by dmitriy on 08.05.16.
 */
public class ChatMessage extends TextMessage {
    private Long chatId;
    private Timestamp timestamp;

    public ChatMessage(Long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
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
        if (!super.equals(other)) {
            return false;
        }
        ChatMessage message = (ChatMessage) other;
        return Objects.equals(text, message.text) && Objects.equals(chatId, message.chatId);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "chatId='" + chatId + '\'' +
                "text='" + text + '\'' +
                '}';
    }

}
