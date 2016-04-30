package arhangel.dim.commands;

import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.session.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.server.Server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class ChatListMessageCommand implements Command {

    private Server server;

    public ChatListMessageCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            if (session.getUser() == null) {
                StatusMessage errorMessage = new StatusMessage();
                errorMessage.setText("You should login before you can get chats");
                session.send(errorMessage);
                return;
            }
            MessageStore messageStore = server.getDbFactory().getMessageStoreDao();
            Long userId = session.getUser().getId();
            List<Long> chatsByUserId = messageStore.getChatsByUserId(userId);

            ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
            chatListResultMessage.setChatIds(chatsByUserId);
            session.send(chatListResultMessage);

        } catch (SQLException | ProtocolException | IOException e) {
            throw new CommandException(e);
        }
    }
}
