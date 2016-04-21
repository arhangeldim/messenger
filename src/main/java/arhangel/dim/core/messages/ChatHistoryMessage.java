package arhangel.dim.core.messages;

/**
 * Created by Арина on 19.04.2016.
 */
public class ChatHistoryMessage extends Message {
    private Long chatId;

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
}
