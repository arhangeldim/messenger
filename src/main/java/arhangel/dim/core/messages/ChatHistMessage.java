package arhangel.dim.core.messages;

public class ChatHistMessage extends Message {
    private Long chatId;

    public ChatHistMessage(Long chatId) {
        this.setType(Type.MSG_CHAT_HIST);
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
