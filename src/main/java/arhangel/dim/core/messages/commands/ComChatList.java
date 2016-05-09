package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatListMessage;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStoreImpl;

import java.io.IOException;
import java.util.List;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComChatList implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatListMessage mes = (ChatListMessage) message;
        if (session.getUser() != null) {
            TextMessage response = new TextMessage();
            response.setType(Type.MSG_CHAT_LIST_RESULT);
            StringBuilder stringBuilder = new StringBuilder();
            MessageStoreImpl storage = (MessageStoreImpl) session.getMessageStore();
            List<Long> ids = storage.getChatsByUserId(session.getUser().getId());
            for (Long id : ids) {
                stringBuilder.append(id);
                stringBuilder.append(" ");
            }
            response.setText(stringBuilder.toString());
            session.send(response);
        } else {
            StatusMessage response = new StatusMessage();
            response.setStatus(StatusCode.AuthenticationRequired);
            session.send(response);
        }
    }
}
