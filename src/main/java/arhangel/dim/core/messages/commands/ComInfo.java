package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComInfo implements Command {
    private int userId;

    public ComInfo(int id) {
        this.userId = id;
    }

    public ComInfo() { //info command without parameters
        this.userId = -1;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {

    }
}
