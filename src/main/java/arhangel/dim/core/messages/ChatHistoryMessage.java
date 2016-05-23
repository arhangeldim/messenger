package arhangel.dim.core.messages;

public class ChatHistoryMessage extends Message {
    private Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "ChatHistoryMessage{" +
                "chatId=" + chatId +
                "} " + super.toString();
    }
}
