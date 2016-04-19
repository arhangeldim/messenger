package arhangel.dim.core;

import java.util.List;

/**
 * А над этим классом надо еще поработать
 */
public class Chat {
    private Long id;
    private List<Long> participants;
    private List<Long> messages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }

    public List<Long> getMessages() {
        return messages;
    }

    public void setMessages(List<Long> messages) {
        this.messages = messages;
    }
}
