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
 * Created by Арина on 17.04.2016.
 */
public class TextHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        TextMessage msg = (TextMessage) message;
        DbConnect db = new DbConnect();
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery("SELECT * FROM chattouser WHERE chat_id = " + msg.getChatId().toString());
            if (!rs.next()) {
                StatusMessage errorMsg = new StatusMessage();
                errorMsg.setText("There is no chat with id = " + msg.getChatId().toString() + ", though you may create it with chat_create <user_id list>. Message is not sent.");
                errorMsg.setType(Type.MSG_STATUS);
                session.send(errorMsg);
                return;
            }
            String sql = "INSERT INTO messages VALUES ("+msg.getSenderId().toString()+","+msg.getChatId().toString()+",'"+msg.getText()+"')";
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
