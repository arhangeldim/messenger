package arhangel.dim.core.messages;

/**
 *
 */
public class MessageException extends Exception {
    public MessageException(String msg) {
        super(msg);
    }

    public MessageException(Throwable ex) {
        super(ex);
    }
}
