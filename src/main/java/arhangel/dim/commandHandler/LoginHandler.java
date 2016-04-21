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
public class LoginHandler extends CommandHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() != null) {
            session.notLoggedIn("You are already logged in as "+session.getUser().getName());
            return;
        }
        LoginMessage msg = (LoginMessage) message;
        DbConnect db = new DbConnect();
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            if (msg.getLogin()!=null) {
                ResultSet rs = stmnt.executeQuery("SELECT user_id FROM users WHERE login = " + "'" + msg.getLogin() + "' && password = " + "'" + msg.getPassword() + "'");
                if (ifErrorSend(rs.next(),session,"Wrong combination of login and password. Try again or type /login to register")) {
                    return;
                } else {
                    session.setUser(Long.valueOf(rs.getString("user_id")), msg.getLogin());
                    StatusMessage statMsg = new StatusMessage();
                    statMsg.setText("Successfully logged in as " + msg.getLogin());
                    statMsg.setType(Type.MSG_STATUS);
                    session.send(statMsg);
                }
            } else {
                Message regMsg = new LoginMessage();
                regMsg.setType(Type.MSG_LOGIN);
                session.send(regMsg);
            }
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

