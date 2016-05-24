package arhangel.dim.core.messages;

/**
 * Created by olegchuikin on 22/05/16.
 */
public class ChatHistMessage extends Message {

    private Long chatId;

    public ChatHistMessage() {
        super(Type.MSG_CHAT_HIST);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }

        ChatHistMessage that = (ChatHistMessage) object;

        return !(chatId != null ? !chatId.equals(that.chatId) : that.chatId != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChatHistMessage{" +
                "chatId='" + chatId + '\'' +
                '}';
    }
}
