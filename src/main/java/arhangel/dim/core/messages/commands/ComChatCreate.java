package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStoreImpl;

import java.io.IOException;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComChatCreate implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatCreateMessage mes = (ChatCreateMessage) message;

        MessageStoreImpl mesStore = (MessageStoreImpl) session.getMessageStore();
        StatusMessage response = new StatusMessage();
        if (session.getUser() != null) {
            Long chatId = mesStore.addChat(mes.getUsersList());
            if (chatId != null) {
                response.setStatus(StatusCode.ChatAvailable);
            } else {
                response.setStatus(StatusCode.ChatIsNotAvailable);
            }
        } else {
            response.setStatus(StatusCode.AuthenticationRequired);
        }
        session.send(response);

    }
}
