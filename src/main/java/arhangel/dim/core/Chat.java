package arhangel.dim.core;

import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.store.dao.Identified;
import arhangel.dim.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat implements Identified<Long> {

    private Long id;
    private List<Long> messages = new ArrayList<>();
    private List<Long> participants = new ArrayList<>();
    private User admin;

    public void addMessage(TextMessage msg) {
        messages.add(msg.getId());
    }

    public void addParticipant(User user) {
        participants.add(user.getId());
    }

    public void addParticipant(Long userId) {
        participants.add(userId);
    }

    public void removeParticipant(User user) {
        participants.remove(user.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getMessages() {
        return messages;
    }

    public void setMessages(List<Long> messages) {
        this.messages = messages;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Chat chat = (Chat) object;

        if (id != null ? !id.equals(chat.id) : chat.id != null) {
            return false;
        }
        if (messages != null ? !messages.equals(chat.messages) : chat.messages != null) {
            return false;
        }
        if (participants != null ? !participants.equals(chat.participants) : chat.participants != null) {
            return false;
        }
        return !(admin != null ? !admin.equals(chat.admin) : chat.admin != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (messages != null ? messages.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (admin != null ? admin.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("chat[%d]: participants: %s",
                id, String.join(",", ParseUtils.longListToStringArr(participants)));
    }
}
