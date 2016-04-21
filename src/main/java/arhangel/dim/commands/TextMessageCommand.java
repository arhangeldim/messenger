package arhangel.dim.commands;

import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;
import arhangel.dim.server.Server;

/**
 * Created by olegchuikin on 18/04/16.
 */
public class TextMessageCommand implements Command {

    private Server server;

    public TextMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {

    }

}
