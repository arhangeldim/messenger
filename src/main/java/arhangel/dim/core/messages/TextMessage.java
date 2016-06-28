package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private String text;
    private Long chatId;

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextMessage() {}

    public TextMessage(Long id, Long senderId, Long chatId, String text) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.text = text;
        this.type = Type.MSG_TEXT;
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
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", text='" + text +
                ", chatId=" + chatId +
                '}';
    }
}
