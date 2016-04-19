package arhangel.dim.core.messages;

import java.util.Date;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private Long chatId;
    private String text;
    private Date date;

    public TextMessage() {
        super();
        this.setType(Type.MSG_TEXT);
    }

    public TextMessage(Long chatId, String text) {
        this();
        this.setChatId(chatId);
        this.setText(text);
    }

    public TextMessage(Long chatId, String text, Date date) {
        this();
        this.setChatId(chatId);
        this.setText(text);
        this.setDate(date);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        return Objects.equals(getText(), message.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getText());
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "text='" + getText() + '\'' +
                '}';
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

}
