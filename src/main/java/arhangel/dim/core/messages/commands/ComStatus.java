package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.net.Session;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComStatus implements Command {
    private StatusCode status;

    public ComStatus(StatusCode status) {
        this.status = status;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {

    }
}
