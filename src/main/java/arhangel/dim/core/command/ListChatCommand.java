package arhangel.dim.core.command;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.ListChatMessage;
import arhangel.dim.core.messages.ListChatResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ListChatCommand implements Command {

    private Server server;

    static Logger log = LoggerFactory.getLogger(CreateChatCommand.class);

    public ListChatCommand(Server server) {
        this.server = server;
    }

    @Override
    public void execute(Session session, Message message) throws CommandException {
        try {
            ListChatMessage chatListMessage = (ListChatMessage)message;
            StatusMessage errorMessage = new StatusMessage();

            User user = session.getUser();
            if (user == null) {
                errorMessage.setStatus("Ony authorised person can see chats");
                session.send(errorMessage);
                return;
            }

            MessageStoreImpl messageStore = (MessageStoreImpl)server.getMessageStore();
            List<Long> chatIds = messageStore.getChatsByUserId(user.getId());
            if (chatIds == null) {
                errorMessage.setStatus("No chats for user");
                session.send(errorMessage);
                return;
            }

            ListChatResultMessage listChatResultMessage = new ListChatResultMessage();
            listChatResultMessage.setChatIds(chatIds);
            session.send(listChatResultMessage);

        } catch (IOException | ProtocolException e) {
            throw new CommandException(e);
        }

    }
}
