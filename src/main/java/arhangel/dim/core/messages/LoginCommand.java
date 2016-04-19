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
        if (!session.userAuthenticated()) {
            LoginMessage loginMessage = (LoginMessage) message;
            UserStore userStore = PostgresqlDaoFactory.getDaoFactory(DaoFactory.DaoTypes.PostgreSQL).getUserDao();
            User user = userStore.getUser(loginMessage.getLogin(), loginMessage.getSecret());
            if (user != null) {
                session.setUser(user);
                log.info("{} logged in", user.getLogin());
            } else {
                log.error("User with supplied credentials doesn't exist {}", loginMessage.getLogin());
            }
            return;
        }
        log.error("Already logged in {}", session.getUser().getLogin());
    }
}
