package arhangel.dim.core.messages;

/**
 * Created by dmitriy on 25.04.16.
 */
public class StatusMessage extends TextMessage {
    private StatusCode status = StatusCode.OK;
    private String userName;
    private Long userId;

    public StatusMessage(String userName, Long userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public StatusMessage() {

    }

    public void setStatus(StatusCode code) {
        this.status = code;
    }

    public StatusCode getStatusCode() {
        return status;
    }

    public String getUserName() {
        return userName;
    }

    public Long getUserId() {
        return userId;
    }
}
