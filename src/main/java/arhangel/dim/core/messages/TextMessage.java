package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private Long id;
    private String senderLogin;
    private String text;
    private Long chatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderLogin() {
        return senderLogin;
    }

    public void setSenderLogin(String senderLogin) {
        this.senderLogin = senderLogin;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "id=" + id +
                ", senderLogin='" + senderLogin + '\'' +
                ", text='" + text + '\'' +
                ", chatId=" + chatId +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextMessage)) {
            return false;
        }

        TextMessage that = (TextMessage) obj;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (senderLogin != null ? !senderLogin.equals(that.senderLogin) : that.senderLogin != null) {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null) {
            return false;
        }
        return chatId != null ? chatId.equals(that.chatId) : that.chatId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (senderLogin != null ? senderLogin.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        return result;
    }
}
