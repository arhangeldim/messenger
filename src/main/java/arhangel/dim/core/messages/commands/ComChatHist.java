package arhangel.dim.core.messages.commands;

import arhangel.dim.core.messages.ChatHistMessage;
import arhangel.dim.core.messages.ChatMessage;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
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
import java.util.List;

/**
 * Created by dmitriy on 25.04.16.
 */
public class ComChatHist implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException, IOException, ProtocolException {
        ChatHistMessage mes = (ChatHistMessage) message;
        if (session.getUser() != null) {
            TextMessage response = new TextMessage();
            response.setType(Type.MSG_CHAT_HIST_RESULT);

            StringBuilder stringBuilder = new StringBuilder();
            MessageStoreImpl mesStorage = (MessageStoreImpl) session.getMessageStore();
            UserStoreImpl userStorage = (UserStoreImpl) session.getUserStore();
            List<Long> ids = mesStorage.getMessagesFromChat(mes.getChatId());

            for (Long id : ids) {
                ChatMessage chatMes = (ChatMessage) mesStorage.getMessageById(id);
                stringBuilder.append("User " + userStorage.getUserById(chatMes.getSenderId()).getName() + " wrote:\n");
                stringBuilder.append(chatMes.getText());
                stringBuilder.append("\n at " + chatMes.getTimestamp().toString());
                stringBuilder.append("\n=====\n");
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
