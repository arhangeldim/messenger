package arhangel.dim.core.messages;

public class StatusMessage extends Message {
    private String status;

    public StatusMessage() {
        this.setType(Type.MSG_STATUS);
    }

    public StatusMessage(String status) {
        this.setType(Type.MSG_STATUS);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
