package arhangel.dim.core.messages;

public class InfoMessage extends Message {
    private Long aboutId;

    public InfoMessage() {
        super();
        this.setType(Type.MSG_INFO);
    }

    public Long getAboutId() {
        return aboutId;
    }

    public void setAboutId(Long aboutId) {
        this.aboutId = aboutId;
    }
}
