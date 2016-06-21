package arhangel.dim.core.commands;


public class CommandException extends Exception {
    public CommandException(String msg) {
        super(msg);
    }

    public CommandException(Throwable ex) {
        super(ex);
    }
}
