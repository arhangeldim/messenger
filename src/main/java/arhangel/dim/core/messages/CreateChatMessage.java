package arhangel.dim.core.messages;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateChatMessage extends Message {

    private List<Long> usersIds;

    public CreateChatMessage() {
        this.setType(Type.MSG_CHAT_CREATE);
    }

    public void setUsersIds(List<Long> userIds) {
        this.usersIds = userIds;
    }

    public List<Long> getUsersIds() {
        return usersIds;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        CreateChatMessage createChatMessage = (CreateChatMessage) other;

        return Objects.equals(usersIds, createChatMessage.usersIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsersIds());
    }

    @Override
    public String toString() {
        return "ChatCreateMessage: " +
                String.join(",", usersIds.stream().map(Object::toString).collect(Collectors.toList()));
    }
}
