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
public class InfoHandler extends CommandHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        StatusMessage msg = (StatusMessage) message;
        try {
            if (msg.getText() != "self") {
                DbConnect db = new DbConnect();
                Connection connection = db.connect();
                Statement stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("SELECT login FROM users WHERE user_id = " + msg.getText());
                if (ifErrorSend(rs.next(),session,"No user with id " + msg.getText())) {
                    return;
                } else {
                    StatusMessage statMsg = new StatusMessage();
                    statMsg.setText("User with id = "+msg.getText()+" has login "+rs.getString("login"));
                    statMsg.setType(Type.MSG_INFO_RESULT);
                    session.send(statMsg);
                }
                stmnt.close();
            } else {
                StatusMessage statMsg = new StatusMessage();
                statMsg.setText("Your login is " + session.getUserLogin());
                statMsg.setType(Type.MSG_INFO_RESULT);
                session.send(statMsg);
            }

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
