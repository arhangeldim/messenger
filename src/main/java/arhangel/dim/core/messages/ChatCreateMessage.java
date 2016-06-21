package arhangel.dim.core.messages;

public class ChatCreateMessage extends Message {
    private Long[] participants;

    public ChatCreateMessage(Long[] participants) {
        this.setType(Type.MSG_CHAT_CREATE);
        this.participants = participants;
    }

    public Long[] getParticipants() {
        return participants;
    }

    public void setParticipants(Long[] participants) {
        this.participants = participants;
    }
}
