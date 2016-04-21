package arhangel.dim.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.PersistException;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.server.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

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

        UserStore userStore;

        User user;
        try {
            userStore = server.getDbFactory().getUserStoreDao();
            user = userStore.getUser(login, password);


            if (user == null) {
                log.info("There is no user " + loginMessage.getLogin());
                user = new User();
                user.setName(loginMessage.getLogin());
                user.setPassword(loginMessage.getPassword());
                user = userStore.addUser(user);
                log.info("User " + user.getName() + " created.");
            }

            session.setUser(user);

            StatusMessage response = new StatusMessage();
            response.setText(String.format("You login like: %s. Your id: %d", user.getName(), user.getId()));
            response.setType(MSG_STATUS);
            session.send(response);
        } catch (PersistException | SQLException | ProtocolException | IOException e) {
            throw new CommandException(e);
        }

    }
}
