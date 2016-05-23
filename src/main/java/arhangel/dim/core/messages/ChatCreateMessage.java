package arhangel.dim.core.messages;

import java.util.List;

public class ChatCreateMessage extends Message {
    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "users=" + users +
                "} " + super.toString();
    }
}
