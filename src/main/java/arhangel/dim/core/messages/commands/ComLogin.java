package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserStore;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComLogin implements Command {
    private UserStore userSt;

    public ComLogin(UserStore storage) {
        this.userSt = storage;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {

    }
}
