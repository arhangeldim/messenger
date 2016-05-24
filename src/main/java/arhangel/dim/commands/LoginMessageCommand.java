package arhangel.dim.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.dao.UserDao;
import arhangel.dim.session.Session;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.server.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static arhangel.dim.core.messages.Type.MSG_STATUS;

/**
 * Created by olegchuikin on 18/04/16.
 */
public class LoginMessageCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(LoginMessageCommand.class);

    public LoginMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        log.info("execute login message", message);

        LoginMessage loginMessage = (LoginMessage) message;
        String login = loginMessage.getLogin();
        String password = loginMessage.getPassword();

        User user;
        try {

            boolean newUser = false;

            UserDao userDao = (UserDao) server.getDbFactory().getDao(User.class);

            user = userDao.getUserByLogin(login);

            if (user == null) {
                newUser = true;
                log.info("There is no user " + loginMessage.getLogin());
                user = new User();
                user.setName(loginMessage.getLogin());
                user.setPassword(loginMessage.getPassword());
                user = userDao.persist(user);
                log.info("User " + user.getName() + " created.");
            }

            StatusMessage response = new StatusMessage();
            response.setType(MSG_STATUS);

            if (!user.getPassword().equals(password)) {
                response.setText("Incorrect login or password. Please, try again!");
            } else {
                session.setUser(user);
                response.setText(String.format((newUser ? "New account was created." : "") +
                        "Your login: %s. Your id: %d", user.getName(), user.getId()));
            }
            session.send(response);
        } catch (PersistException | ProtocolException | IOException e) {
            throw new CommandException(e);
        }

    }
}
