package arhangel.dim.core.messages;

public class ChatHistoryMessage extends Message {
    private Long chatId;

    public ChatHistoryMessage(Long chatId) {
        super();
        this.setType(Type.MSG_CHAT_HIST);
        this.setChatId(chatId);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
