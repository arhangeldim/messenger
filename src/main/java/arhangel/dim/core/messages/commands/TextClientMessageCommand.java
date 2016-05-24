package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextClientMessage;
import arhangel.dim.core.messages.commands.Command;
import arhangel.dim.core.messages.commands.CommandException;
import arhangel.dim.core.net.Session;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class TextClientMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        TextClientMessage message = (TextClientMessage) msg;
        System.out.print(message.getTimestamp());
        System.out.print("| ");
        System.out.print( message.getSenderLogin());
        System.out.print(": ");
        System.out.println(message.getText());
    }
}
