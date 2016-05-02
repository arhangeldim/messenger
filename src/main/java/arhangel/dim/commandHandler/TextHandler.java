package arhangel.dim.commandhandler;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Арина on 17.04.2016.
 */
public class TextHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() == null) {
            session.notLoggedIn("Unlogged users cannot send messages. Your message is not sent.");
            return;
        }
        TextMessage msg = (TextMessage) message;
        DbConnect db = new DbConnect();
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery("SELECT * FROM chattouser WHERE chat_id = " + msg.getChatId().toString());
            if (!rs.next()) {
                StatusMessage errorMsg = new StatusMessage();
                String text = "There is no chat with id = " + msg.getChatId().toString();
                errorMsg.setText(text);
                errorMsg.setType(Type.MSG_STATUS);
                session.send(errorMsg);
                return;
            }
            String msgSender = msg.getSenderId().toString();
            String chatId = msg.getChatId().toString();
            String sql = "INSERT INTO messages VALUES (" + msgSender + "," + chatId + ",'" + msg.getText() + "')";
            stmnt.executeUpdate(sql);
            stmnt.close();
        } catch (SQLException e) {
            CommandException ex = new CommandException("SQLException");
            throw ex;
        } catch (IOException e) {
            CommandException ex = new CommandException("IOException");
            throw ex;
        } catch (ProtocolException e) {
            CommandException ex = new CommandException("ProtocolException");
            throw ex;
        }

    }
}
