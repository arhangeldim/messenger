package arhangel.dim.core.net;

/**
 *
 */
public class ProtocolException extends Exception {
    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(String msg) {
        super(msg);
    }

    public ProtocolException(Throwable ex) {
        super(ex);
    }
}
