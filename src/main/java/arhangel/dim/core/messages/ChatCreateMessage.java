package arhangel.dim.core.messages;

import java.util.List;

public class ChatCreateMessage extends Message {
    private List<Long> participants;

    public ChatCreateMessage(List<Long> participants) {
        super();
        this.setParticipants(participants);
        this.setType(Type.MSG_CHAT_CREATE);
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }
}
