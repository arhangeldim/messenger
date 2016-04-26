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
        if (!session.userAuthenticated()) {
            RegisterMessage registerMessage = (RegisterMessage) message;
            UserStore userStore = session.getServer().getUserStore();
            // PostgresqlDaoFactory.getDaoFactory(DaoFactory.DaoTypes.PostgreSQL).getUserDao();
            if (userStore.getUserByLogin(registerMessage.getLogin()) == null) {
                User user = new User(registerMessage.getLogin(), registerMessage.getSecret());
                userStore.addUser(user);
                session.setUser(user);
                log.info("Created user {}", user);
                return;
            }
            log.error("User already exists {}", registerMessage.getLogin());
            return;
        }
        log.error("User already authenticated {}", session.getUser().getLogin());
    }
}
