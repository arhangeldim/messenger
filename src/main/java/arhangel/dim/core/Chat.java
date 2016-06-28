package arhangel.dim.core;

import arhangel.dim.core.messages.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private Long adminId;
    private List<TextMessage> messages;
    private List<Long> participantsId;

    public Chat() {}

    public Chat(Long id, Long adminId, List<TextMessage> messages, List<Long> participantsId) {
        this.id = id;
        this.adminId = adminId;
        this.messages = messages;
        this.participantsId = participantsId;
    }

    public Chat(Long id) {
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public List<TextMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TextMessage> messages) {
        this.messages = messages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id= " + id +
                ", adminId= " + adminId +
                ", messages= " + messages.toString() +
                ", participantsId= " + participantsId.toString() +
                '}';
    }
}
