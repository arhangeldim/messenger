package arhangel.dim.core.messages;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatHistMessage extends Message {
    Long chatId;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public ChatHistMessage() {
        setType(Type.MSG_CHAT_HIST);
    }
}
