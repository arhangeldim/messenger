package arhangel.dim.core.message;

import arhangel.dim.core.store.DataBaseMessageStore;
import arhangel.dim.core.store.MessageStore;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat {

    private Integer id;
    private MessageStore messageStore;
    private List<Integer> participantIds;
    private Connection connection;

    public Chat(Integer id, Connection conn) {
        this.id = id;
        this.participantIds = new ArrayList<>();
        this.connection = conn;
        this.messageStore = new DataBaseMessageStore(connection, this);
    }

    public Chat(List<Integer> participantIds, Integer id, Connection conn) {
        this.id = id;
        this.participantIds = participantIds;
        this.connection = conn;
        this.messageStore = new DataBaseMessageStore(conn, this);
    }

    public boolean addParticipant(int id) {
        if (participantIds.contains(id)) {
            return false;
        }
        participantIds.add(id);
        return true;
    }

    public void addMessage(int authorId, String authorName, String message) throws Exception {
        messageStore.addMessage(authorId, authorName, message, this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public Map<Integer, Message> getMessageMap() throws Exception {
        return messageStore.getMessagesMap();
    }
}
