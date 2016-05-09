package arhangel.dim.core.messages.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusCode;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStoreImpl;

import java.io.IOException;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComInfo implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        InfoMessage mes = (InfoMessage) message;
        if (session.getUser() != null) {
            TextMessage response = new TextMessage();
            response.setType(Type.MSG_INFO_RESULT);
            StringBuilder stringBuilder = new StringBuilder();
            UserStoreImpl userStore = (UserStoreImpl) session.getUserStore();
            MessageStoreImpl mesStore = (MessageStoreImpl) session.getMessageStore();
            User currentUser = userStore.getUserById(mes.getUserId());
            stringBuilder.append("Info about user " + currentUser.getName() + "\n");
            stringBuilder.append("This user sent " + mesStore
                    .countMessagesByUserId(mes.getUserId()).toString() + " messages\n");
            stringBuilder.append("This user is in " + mesStore
                    .countChatsByUserId(mes.getUserId()).toString() + " chats\n");
            stringBuilder.append("And also this user is created " + mesStore
                    .countChatsByOwnerId(mes.getUserId()).toString() + " chats\n");
            response.setText(stringBuilder.toString());
            session.send(response);
        } else {
            StatusMessage response = new StatusMessage();
            response.setStatus(StatusCode.AuthenticationRequired);
            session.send(response);
        }
    }
}
