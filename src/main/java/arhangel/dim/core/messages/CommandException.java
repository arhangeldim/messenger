package arhangel.dim.core.messages;

/**
 *
 */
public class CommandException extends Exception {
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable throwable) {
        super(throwable);
    }
}
