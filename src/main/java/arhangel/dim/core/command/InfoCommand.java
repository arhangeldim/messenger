package arhangel.dim.core.command;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InfoCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public InfoCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            InfoMessage infoMessage = (InfoMessage) message;
            StatusMessage errorMessage = new StatusMessage();

            UserStore userStore = server.getUserStore();

            if (session.getUser() == null) {
                errorMessage.setStatus("Ony authorised person can get info");
                session.send(errorMessage);
                return;
            }

            long userId = session.getUser().getId();
            if (infoMessage.getUserId() != -1) {
                userId = infoMessage.getUserId();
            }

            User user = server.getUserStore().getUserById(userId);

            if (user == null) {
                errorMessage.setStatus("No user with id " + userId);
                session.send(errorMessage);
                return;
            }

            InfoMessage response = new InfoMessage();
            response.setUserId(userId);
            response.setInfo("User Login: " + user.getName());
            session.send(response);

        } catch (IOException | ProtocolException e) {
            throw new CommandException(e);
        }
    }
}