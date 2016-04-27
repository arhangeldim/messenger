package arhangel.dim.core.messages;

/**
 * Created by dmitriy on 25.04.16.
 */
public class StatusMessage extends TextMessage {
    private StatusCode status = StatusCode.OK;

    public void setStatus(StatusCode code) {
        this.status = code;
    }

    public StatusCode getStatus() {
        return status;
    }
}
