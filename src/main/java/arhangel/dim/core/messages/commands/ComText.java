package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatMessage;
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
public class ComText implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatMessage mes = (ChatMessage) message;

        MessageStoreImpl mesStore = (MessageStoreImpl) session.getMessageStore();
        StatusMessage response = new StatusMessage();
        if (session.getUser() != null) {
            if (mesStore.addMessage(mes.getChatId(), mes)) {
                response.setStatus(StatusCode.MessageSent);
            } else {
                response.setStatus(StatusCode.MessageNotSent);
            }
            session.send(response);
        } else {
            response.setStatus(StatusCode.AuthenticationRequired);
            session.send(response);
        }
    }
}
