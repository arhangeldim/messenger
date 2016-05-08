package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;

import java.util.List;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComChatCreate implements Command {
    private List<Integer> userList;

    public ComChatCreate(List<Integer> users) {
        this.userList = users;
    }

    @Override
    public static void execute(Session session, Message message) throws CommandException {

    }
}
