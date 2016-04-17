package arhangel.dim.commandHandler;

import arhangel.dim.DbConnect;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.net.Session;

import java.sql.Connection;
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
            String sql = "INSERT INTO messages VALUES ("+msg.getSenderId().toString()+","+msg.getChatId().toString()+",'"+msg.getText()+"')";
            stmnt.executeUpdate(sql);
            stmnt.close();
        } catch (SQLException e) {
            CommandException ex = new CommandException("SQLException");
            throw ex;
        }

    }
}
