package arhangel.dim.core.messages;

import java.util.List;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class ChatCreateMessage extends Message {
    private List<Long> participantIds;

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
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
        return Objects.equals(participantIds, message.participantIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), participantIds, participantIds);
    }

    @Override
    public String toString() {
        return "InfoMessage{" +
                "participantIds=" + participantIds +
                '}';
    }
}
