package arhangel.dim.core.messages.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatHistResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextClientMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thefacetakt on 23.05.16.
 */
public class ChatHistMessageCommand implements Command {
    @Override
    public void execute(Session session, Message msg) throws CommandException {
        if (session.getUser() == null) {
            session.send(StatusMessage.logInFirstMessage());
        }
        ChatHistMessage message = (ChatHistMessage) msg;
        Chat chat = session.getServer().getMessageStore()
                .getChatById(message.getChatId());
        if (!chat.getUsers().contains(session.getUser().getId())) {
            session.send(StatusMessage.wrongChatMessage());
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

        session.send(new ChatHistResultMessage(result));

    }
}
