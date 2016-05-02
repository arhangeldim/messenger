package arhangel.dim.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.ChatListResultMessage;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.store.dao.ChatDao;
import arhangel.dim.session.Session;
import arhangel.dim.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

            ChatDao chatDao = (ChatDao) server.getDbFactory().getDao(Chat.class);

            List<Long> chatsIds = new ArrayList<>();
            List<Chat> chats = chatDao.getChatsByAdmin(session.getUser());
            if (chats != null) {
                chatsIds = chats
                        .stream()
                        .map(Chat::getId)
                        .collect(Collectors.toList());
            }

            ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
            chatListResultMessage.setChatIds(chatsIds);
            session.send(chatListResultMessage);

        } catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
