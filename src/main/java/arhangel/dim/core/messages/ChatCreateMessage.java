package arhangel.dim.core.messages;

import java.util.Set;

public class ChatCreateMessage extends Message {
    private Set<String> users;

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "users=" + users +
                "} " + super.toString();
    }
}
