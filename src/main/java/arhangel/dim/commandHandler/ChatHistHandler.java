package arhangel.dim.commandHandler;

import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Арина on 19.04.2016.
 */
public class ChatHistHandler extends CommandHandler implements Command  {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() == null) {
            session.notLoggedIn("Request available only to logged in users.");
            return;
        }
        ChatHistoryMessage msg = (ChatHistoryMessage) message;
        DbConnect db = new DbConnect();
        Statement stmnt;
        String chat = msg.getChatId().toString();
        try {
            Connection connection = db.connect();
            stmnt = connection.createStatement();
            ResultSet mess = stmnt.executeQuery("SELECT * FROM messages WHERE chat_id = " + chat);
            if (ifErrorSend(mess.next(),session,"There is no chat with id = " + chat)) {
                return;
            }
            ResultSet rs = stmnt.executeQuery("SELECT * FROM chattouser WHERE chat_id = " + chat + "&& user_id = " + msg.getSenderId().toString());
            if (ifErrorSend(rs.next(),session,"You don't belong to chat with id = " + chat + ". Deal with it.")) {
                return;
            }
            ChatHistResultMessage result = new ChatHistResultMessage();
            int userId;
            while (mess.next()) {
                String messageText = mess.getString("text");
                userId = mess.getInt("sender_id");
                if (userId == msg.getSenderId()) {
                    result.addMsg("[You] "+messageText);
                } else {
                    result.addMsg("[" + String.valueOf(userId) + "] " + messageText);
                }
            }
            result.setChatId(chat);
            result.setType(Type.MSG_CHAT_HIST_RESULT);
            session.send(result);
            stmnt.close();
        } catch (SQLException e) {
            System.out.println("sqlexception");
            CommandException ex = new CommandException("SQLException");
            throw ex;
        } catch (ProtocolException e) {
            CommandException ex = new CommandException("ProtocolException");
            throw ex;
        } catch (IOException e) {
            CommandException ex = new CommandException("IOException");
            throw ex;
        }
    }
}
