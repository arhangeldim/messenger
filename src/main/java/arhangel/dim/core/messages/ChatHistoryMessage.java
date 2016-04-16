package arhangel.dim.core.messages;

public class ChatHistoryMessage extends Message {
    private Long chatId;

    public ChatHistoryMessage(Long chatId) {
        super();
        this.setType(Type.MSG_CHAT_HIST);
        this.chatId = chatId;
    }
}
