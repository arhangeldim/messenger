package arhangel.dim.commandHandler;

import arhangel.dim.DbConnect;
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
public class LoginHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        LoginMessage msg = (LoginMessage) message;
        DbConnect db = new DbConnect();
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery("SELECT user_id FROM users WHERE login = "+"'"+msg.getLogin()+"' && password = "+"'"+msg.getPassword()+"'");
            if (!rs.next()) {
                StatusMessage errorMsg = new StatusMessage();
                errorMsg.setText("Wrong combination of login and password. Try again or type /login to register");
                session.send(errorMsg);
            } else {
                session.setUser(Long.valueOf(rs.getString("user_id")),msg.getLogin());
                StatusMessage statMsg = new StatusMessage();
                statMsg.setText("Successfully logged in as "+msg.getLogin());
                session.send(statMsg);
            }
            stmnt.close();
        } catch (SQLException e) {
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

