package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class InfoCommand implements Command {
    private static Logger log = LoggerFactory.getLogger(InfoCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            InfoMessage infoMessage = (InfoMessage) message;
            String requestedLogin;
            if (infoMessage.getLogin() == null) {
                requestedLogin = session.getUser().getLogin();
            } else {
                requestedLogin = infoMessage.getLogin();
            }
            UserStore userStore = session.getServer().getUserStore();
            User user = userStore.getUserByLogin(requestedLogin);
            if (user != null) {
                MessageStore messageStore = session.getServer().getMessageStore();
                Set<Long> userChatIds = messageStore.getChatsByUserId(user.getId());

                InfoResultMessage infoResultMessage = new InfoResultMessage();
                infoResultMessage.setSenderId(null);
                infoResultMessage.setType(Type.MSG_INFO_RESULT);
                infoResultMessage.setChatIds(userChatIds);
                infoResultMessage.setLogin(user.getLogin());
                infoResultMessage.setUserId(user.getId());

                log.info("Sending info on {} to {}", user.getLogin(), session.getUser().getLogin());
                try {
                    session.send(infoResultMessage);
                } catch (Exception e) {
                    log.error("Couldn't reply to info command", e);
                    throw new CommandException("Couldn't reply to info command");
                }
            } else {
                log.info("User requested info on unknown user {}", requestedLogin);
                StatusMessage response = new StatusMessage();
                response.setType(Type.MSG_STATUS);
                response.setSenderId(null);
                response.setText("User not found");
                try {
                    session.send(response);
                } catch (Exception e) {
                    log.error("Couldn't reply to info command", e);
                    throw new CommandException("Couldn't reply to info command");
                }
            }
            return;
        }
        log.info("User requested info command without authenticating first");
        StatusMessage response = new StatusMessage();
        response.setType(Type.MSG_STATUS);
        response.setSenderId(null);
        response.setText("You have to log in first");
        try {
            session.send(response);
        } catch (Exception e) {
            log.error("Couldn't reply to info command", e);
            throw new CommandException("Couldn't reply to info command");
        }
    }
}
