package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private String text;
    private long chatId;

    public TextMessage() {
        this.setType(Type.MSG_TEXT);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getChatId() {
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
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "Message: " + text;
    }
}
