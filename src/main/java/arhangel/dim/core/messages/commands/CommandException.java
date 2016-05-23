package arhangel.dim.core.messages.commands;

/**
 *
 */
public class CommandException extends Exception {
    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(Throwable ex) {
        super(ex);
    }
}
