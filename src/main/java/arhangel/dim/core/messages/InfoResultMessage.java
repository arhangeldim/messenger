package arhangel.dim.core.messages;

public class InfoResultMessage extends Message {
    private String info;

    public InfoResultMessage() {
        super();
        this.setType(Type.MSG_INFO_RESULT);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "INFO: " + info;
    }
}
