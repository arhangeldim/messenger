package arhangel.dim.core.messages;

import java.util.List;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class ChatCreateMessage extends Message {
    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    private List<Long> users;

    public ChatCreateMessage() {
        setType(Type.MSG_CHAT_CREATE);
    }
}
