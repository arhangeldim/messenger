package arhangel.dim.core.messages;

import java.util.Objects;

public class StatusMessage extends Message {
    private String status;

    public StatusMessage() {
        this.setType(Type.MSG_STATUS);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        StatusMessage statusMessage = (StatusMessage) other;

        return Objects.equals(status, statusMessage.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getStatus());
    }

    @Override
    public String toString() {
        return "Status: " + status;
    }
}
