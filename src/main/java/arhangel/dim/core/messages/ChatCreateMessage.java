package arhangel.dim.core.messages;

import java.util.List;
import java.util.Objects;

/**
 * Created by dmitriy on 08.05.16.
 */
public class ChatCreateMessage extends Message{
    private List<Long> usersList;

    public ChatCreateMessage(List<Long> usersList) {
        this.usersList = usersList;
    }

    public List<Long> getUsersList(){
        return usersList;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        ChatCreateMessage message = (ChatCreateMessage) other;
        return Objects.equals(usersList, message.usersList);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Long userId : usersList) {
            stringBuilder.append(" " + userId.toString());
        }
        String listStr = stringBuilder.toString();
        return "ChatCreateMessage{" +
                "usersList='" + listStr + '\'' +
                '}';
    }
}
