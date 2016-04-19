package arhangel.dim.core.messages;


public class ChatListMessage extends Message {
    public ChatListMessage() {
        super();
        this.setType(Type.MSG_CHAT_LIST);
    }
}
