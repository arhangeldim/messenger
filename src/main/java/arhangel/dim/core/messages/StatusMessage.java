package arhangel.dim.core.messages;

import java.util.Objects;

/**
 * Created by philip on 14.04.16.
 */
public class StatusMessage extends Message {
    private String status;
    private Long userId;

    public String getStatus() { return status;}
    public void setStatus(String status) { this.status = status; }

    public StatusMessage(String status) {
        setStatus(status);
        userId = 0L;
    }

    public void setUserId(Long userId) { this.userId = userId;}

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStatus());
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "status='" + getStatus() + '\'' +
                '}';
    }
}
