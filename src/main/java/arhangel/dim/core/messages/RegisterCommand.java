package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.DaoFactory;
import arhangel.dim.core.store.PostgresqlDaoFactory;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterCommand implements Command {
    static Logger log = LoggerFactory.getLogger(RegisterCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        if (!session.userAuthenticated()) {
            RegisterMessage registerMessage = (RegisterMessage) message;
            UserStore userStore = session.getServer().getUserStore();
            if (userStore.getUserByLogin(registerMessage.getLogin()) == null) {
                User user = new User(registerMessage.getLogin(), registerMessage.getSecret());
                user = userStore.addUser(user);
                session.setUser(user);
                log.info("Created user {}", user.getLogin());
                response.setText(String.format("Successfully registered and logged in as %s with id %d", user.getLogin(), user.getId()));
            } else {
                log.info("User already exists {}", registerMessage.getLogin());
                response.setText(String.format("User %s already exists", registerMessage.getLogin()));
            }
            try {
                session.send(response);
            } catch (Exception e) {
                log.error("Couldn't reply to register command", e);
                throw new CommandException("Couldn't reply to register command");
            }
            return;
        }
        log.info("Already logged in {}", session.getUser().getLogin());
        response.setText(String.format("Already logged in as %s", session.getUser().getLogin()));
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to register command", e);
            throw new CommandException("Couldn't reply to register command");
        }
    }
}
