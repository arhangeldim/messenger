package arhangel.dim.core.message;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {

    protected int id;
    protected int authorId;
    protected String message;

    public Message() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Message message = (Message) other;
        return Objects.equals(id, message.id) &&
                Objects.equals(authorId, message.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", senderId=" + authorId +
                '}';
    }
}
