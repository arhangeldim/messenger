package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.core.store.dao.MessageDao;
import arhangel.dim.server.Server;
import arhangel.dim.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by olegchuikin on 22/05/16.
 */
public class ChatHistMessageCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(ChatHistMessageCommand.class);

    public ChatHistMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                sendError(session, "You should login before you can create chat");
                return;
            }

            ChatHistMessage msg = (ChatHistMessage) message;

            log.info("chat history request");

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);
            MessageDao messageDao = (MessageDao) server.getDbFactory().getDao(TextMessage.class);

            Chat chat = chatDao.getByPK(msg.getChatId());
            if (chat == null){
                sendError(session, "There isn't chat with id " + msg.getChatId());
                return;
            }

            ChatHistResultMessage result = new ChatHistResultMessage();
            List<TextMessage> targetMessages = messageDao.getMessagesWithChatId(msg.getChatId());
            result.setMessages(targetMessages);

            session.send(result);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

    private void sendError(Session session, String message) throws IOException, ProtocolException {
        StatusMessage errorMessage = new StatusMessage();
        errorMessage.setText(message);
        session.send(errorMessage);
    }

}
