package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatHistMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        if (session.getUser() == null) {
            try {
                session.send(StatusMessage.logInFirstMessage());
            } catch (ProtocolException | IOException e) {
                throw new CommandException(e);
            }
        }
        ChatHistMessage message = (ChatHistMessage) msg;
        Chat chat = session.getServer().getMessageStore()
                .getChatById(message.getChatId());
        if (!chat.getUsers().contains(session.getUser().getId())) {
            try {
                session.send(StatusMessage.wrongChatMessage());
            } catch (ProtocolException | IOException e) {
                throw new CommandException(e);
            }
        }
        List<Long> msgIds = session.getServer().getMessageStore()
                .getMessagesFromChat(message.getChatId());
        List<TextClientMessage> result = new ArrayList<>();

        for (Long id: msgIds) {
            TextMessage currentMessage = (TextMessage) session.getServer()
                    .getMessageStore().getMessageById(id);
            User user = session.getServer().getUserStore()
                    .getUserById(currentMessage.getSenderId());
            result.add(new TextClientMessage(currentMessage, user.getName()));
        }
        try {
            session.send(new ChatHistResultMessage(result));
        } catch (ProtocolException | IOException e) {
            throw new CommandException(e);
        }
    }
}
