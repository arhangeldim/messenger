package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * Created by thefacetakt on 24.05.16.
 */
public class ChatCreateMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        ChatCreateMessage message = (ChatCreateMessage) msg;

        if (session.getUser() == null) {
            session.send(StatusMessage.logInFirstMessage());
            return;
        }
        int uniqueSize = new HashSet<>(message.getUsers()).size();
        if (message.getUsers().contains(session.getUser().getId()) ||
                uniqueSize != message.getUsers().size() ||
                uniqueSize < 2) {
            StatusMessage response = new StatusMessage();
            response.setText("invalid command parameters");
            session.send(response);
            return;
        }
        for (Long id: message.getUsers()) {
            User him = session.getServer().getUserStore().getUserById(id);
            if (him == null) {
                StatusMessage response = new StatusMessage();
                response.setText("Invalid user: " + id.toString());
                session.send(response);
                return;
            }
        }
        if (message.getUsers().size() == 1) {
            List<Long> myChats = session.getServer().getMessageStore()
                    .getChatsByUserId(session.getUser().getId());
            List<Long> hisChats = session.getServer().getMessageStore()
                    .getChatsByUserId(message.getUsers().get(0));
            myChats.retainAll(hisChats);
            for (Long chatId: myChats) {
                Chat chat = session.getServer()
                        .getMessageStore().getChatById(chatId);
                if (chat.getUsers().size() == 2) {
                    StatusMessage response = new StatusMessage();
                    response.setText("Chat existed, id: " + chatId.toString());

                    session.send(response);
                    return;

                }
            }
        }
        Long newChatId = session.getServer().getMessageStore().addChat();
        session.getServer().getMessageStore()
                .addUserToChat(session.getUser().getId(), newChatId);
        for (Long id: message.getUsers()) {
            session.getServer().getMessageStore()
                    .addUserToChat(id, newChatId);
        }
        StatusMessage response = new StatusMessage();
        response.setText("New chat created, id: " + newChatId.toString());

        session.send(response);

        TextMessage firstMessage = new TextMessage();
        firstMessage.setTimestamp(LocalDateTime.now());
        firstMessage.setSenderId(session.getUser().getId());
        firstMessage.setChatId(newChatId);
        firstMessage.setText("User " + session.getUser().getName()
                + " started chat #" + newChatId.toString());
        CommandByMessage.getCommand(firstMessage.getType())
                .execute(session, firstMessage);
    }
}
