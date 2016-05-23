package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.DaoFactory;
import arhangel.dim.core.store.PostgresqlDaoFactory;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCommand implements Command {
    static Logger log = LoggerFactory.getLogger(LoginCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        if (!session.userAuthenticated()) {
            LoginMessage loginMessage = (LoginMessage) message;
            UserStore userStore = session.getServer().getUserStore();
            User user = userStore.getUser(loginMessage.getLogin(), loginMessage.getSecret());
            if (user != null) {
                session.setUser(user);
                log.info("{} logged in", user.getLogin());
                response.setText(String.format("Successfully logged in as %s with id %d\n" +
                        "In chats: %s", user.getLogin(), user.getId(), session.getServer().getMessageStore().getChatsByUserId(user.getId())));
            } else {
                log.info("User with supplied credentials doesn't exist {}", loginMessage.getLogin());
                response.setText("User with supplied credentials doesn't exist");
            }
            try {
                session.send(response);
            } catch (Exception e) {
                log.error("Couldn't reply to login command", e);
                throw new CommandException("Couldn't reply to login command");
            }
            return;
        }
        log.info("Already logged in {}", session.getUser().getLogin());
        response.setText(String.format("Already logged in as %s", session.getUser().getLogin()));
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to login command", e);
            throw new CommandException("Couldn't reply to login command");
        }
    }
}
