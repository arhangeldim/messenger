package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class StatusMessageCommand implements Command {

    @Override
    public void execute(Session session, Message msg) throws CommandException {
        StatusMessage message = (StatusMessage) msg;
        System.out.println("System message: ");
        System.out.println(message.getText());
    }
}
