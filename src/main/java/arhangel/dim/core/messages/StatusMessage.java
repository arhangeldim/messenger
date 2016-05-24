package arhangel.dim.core.messages;

public class StatusMessage extends Message {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "StatusMessage{" +
                "text='" + text + '\'' +
                "} " + super.toString();
    }
}
