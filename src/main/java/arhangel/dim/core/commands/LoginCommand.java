package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.server.Server;
import org.slf4j.Logger;


public class LoginCommand implements Command {

    Logger log = Server.log;

    @Override
    public Message execute(Session session, Message message) throws CommandException {
        LoginMessage loginMessage = (LoginMessage) message;
        String name = loginMessage.getName();
        String password = loginMessage.getPassword();
        UserStore userStore = session.getUserStore();
        User user;
        StatusMessage statusMessage = new StatusMessage();
        try {
            user = userStore.getUser(name);

            if (user == null) {
                user = new User(name, password);
                userStore.addUser(user);
                statusMessage.setStatus("Successfully signed up");
                session.setUser(user);
            } else if (!user.getPassword().equals(password)) {
                statusMessage.setStatus("Wrong password!!!");
            } else {
                session.setUser(user);
                statusMessage.setStatus("Successfully signed in");
            }
        } catch (StorageException e) {
            throw new CommandException(e);
        }
        return statusMessage;
    }
}
