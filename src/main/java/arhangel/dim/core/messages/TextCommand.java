package arhangel.dim.core.messages;

import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.DaoFactory;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.PostgresqlDaoFactory;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextCommand implements Command {
    static Logger log = LoggerFactory.getLogger(TextCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        if (session.userAuthenticated()) {
            TextMessage textMessage = (TextMessage) message;
            MessageStore messageStore = PostgresqlDaoFactory.getDAOFactory(DaoFactory.DaoTypes.PostgreSQL).getMessageDAO();
            messageStore.addMessage(textMessage.getChatId(), textMessage);
            log.info("Message saved {}", textMessage);
            for (Session session1 : session.getServer().getSessions()) {
                try {
                    session1.send(textMessage);
                } catch (Exception e) {

                }
            }
            log.info("Message broadcasted {}", textMessage);
            return;
        }
        log.error("Message not authorized {}", message);
    }
}
