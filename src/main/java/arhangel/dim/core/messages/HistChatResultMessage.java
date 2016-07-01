package arhangel.dim.core.messages;

import java.util.Objects;

public class HistChatResultMessage extends Message {
    private String history = "";
    private long chatId;

    public HistChatResultMessage() {
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    public void setChatId(long id) {
        this.chatId = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHistory() {
        return history;
    }

    public void addMsg(String msg) {
        history = history + "\n" + msg;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        HistChatResultMessage histChatResultMessage = (HistChatResultMessage) other;
        return Objects.equals(history, histChatResultMessage.history) &&
                Objects.equals(chatId, histChatResultMessage.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getHistory(), getChatId());
    }

    @Override
    public String toString() {
        return "Chat id =" +
                chatId +
                " history: " +
                history;
    }
}
