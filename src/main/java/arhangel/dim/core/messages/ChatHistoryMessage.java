package arhangel.dim.core.messages;


public class ChatHistoryMessage extends Message {
    private Long chatId;

    public ChatHistoryMessage(Long chatId) {
        this.chatId = chatId;
        type = Type.MSG_CHAT_HIST;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
}
