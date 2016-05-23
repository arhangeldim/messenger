package arhangel.dim.core.commands;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.StorageException;
import arhangel.dim.server.Server;

import java.io.IOException;
import java.util.Date;

public class TextCommand extends GenericCommand {
    private Type type = Type.MSG_TEXT;


    private Server server;

    public TextCommand(Server server) {
        this.server = server;
    }

    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        TextMessage textMessage = (TextMessage) message;
        Long chatId = textMessage.getChatId();
        Long messageId;
        try {
            textMessage.setDate(new Date(System.currentTimeMillis()));
            messageId = session.getMessageStore().addMessage(chatId, textMessage);
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }

        Chat chat = null;
        try {
            chat = session.getMessageStore().getChatById(textMessage.getChatId());
            if (chat == null) {
                StatusMessage answer = new StatusMessage();
                answer.setId(messageId);
                answer.setText("No such chat");
                return answer;
            }
        } catch (StorageException e) {
            throw new CommandException("Database failed", e);
        }

        StatusMessage response = new StatusMessage();
        response.setText("User " + session.getUser() + " wrote to chat " + chat.getId() + " : " +
                textMessage.getText());
        response.setSenderId(textMessage.getSenderId());

        for (Long chatUsersId : chat.getParticipants()) {
            //session.send(response);
            for (Session s : server.getSessions()) {
                if (s.getUser() != null && chatUsersId.equals(s.getUser().getId())) {
                    s.send(response);
                }
            }
        }

        StatusMessage answer = new StatusMessage();
        answer.setId(messageId);
        answer.setText("Message sent");
        return answer;
    }
}
