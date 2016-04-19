package arhangel.dim.core.messages;

/**
 *
 */
public class CommandException extends Exception {
    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(String msg, Throwable ex) {
        super(ex);
    }
}
