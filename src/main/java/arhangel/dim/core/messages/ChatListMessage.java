package arhangel.dim.core.messages;


public class ChatListMessage extends Message {

    public ChatListMessage(Long userId) {
        this.senderId = userId;
        this.type = Type.MSG_CHAT_LIST;

    }

    public Long getUserId() {
        return senderId;
    }

    public void setUserId(Long userId) {
        this.senderId = userId;
    }
}
