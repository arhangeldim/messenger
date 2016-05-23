package arhangel.dim.core.messages;

/**
 * Created by d_k on 19.04.16.
 */
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
