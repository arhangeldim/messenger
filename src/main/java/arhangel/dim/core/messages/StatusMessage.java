package arhangel.dim.core.messages;


public class StatusMessage extends Message {
    private String text;
    private String username;

    public StatusMessage() {
        super();
        this.setType(Type.MSG_STATUS);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "{" + super.toString() +
                ", text=\"" + text +
                "\" }";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
