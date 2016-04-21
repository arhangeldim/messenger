package arhangel.dim.commandhandler;

import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.StatusMessage;
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
public class LoginHandler extends CommandHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() != null) {
            session.notLoggedIn("You are already logged in as " + session.getUser().getName());
            return;
        }
        LoginMessage msg = (LoginMessage) message;
        DbConnect db = new DbConnect();
        String query = "SELECT user_id FROM users WHERE login = ";
        String sql = query + "'" + msg.getLogin() + "' && password = '" + msg.getPassword() + "'";
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            if (msg.getLogin() != null) {
                ResultSet rs = stmnt.executeQuery(sql);
                if (ifErrorSend(rs.next(), session, "Wrong login or password. Try again or type /login to register")) {
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

