package arhangel.dim.core.messages;

/**
 * Created by Арина on 17.04.2016.
 */
public class StatusMessage extends Message {
    private String status;
    public String getText() {
        return status;
    }

    public void setText(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Status:" + status;
    }
}
