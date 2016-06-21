package arhangel.dim.core.messages;

public class InfoMessage extends Message {
    private Long infoUserId;

    public InfoMessage() {
        this.setType(Type.MSG_INFO);
        infoUserId = null;
    }

    public InfoMessage(Long infoUserId) {
        this.setType(Type.MSG_INFO);
        this.infoUserId = infoUserId;
    }

    public Long getInfoUserId() {
        return infoUserId;
    }

    public void setInfoUserId(Long infoUserId) {
        this.infoUserId = infoUserId;
    }
}
