package arhangel.dim.core.messages;

public class InfoMessage extends Message {

    public InfoMessage(Long userId) {
        super();
        this.setType(Type.MSG_INFO);
        this.setSenderId(userId);
    }

}
