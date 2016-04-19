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
public class RegistryHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        LoginMessage msg = (LoginMessage) message;
        DbConnect db = new DbConnect();
        try {
            Connection connection = db.connect();
            Statement stmnt = connection.createStatement();
            int userId = 0;
            ResultSet rs = stmnt.executeQuery("SELECT MAX(user_id) as max_id FROM users");
            if (rs.next()) {
                userId = rs.getInt("max_id") + 1;
            }
            String sql = "INSERT INTO users VALUES ('"+msg.getLogin()+"','"+msg.getPassword()+"',"+String.valueOf(userId)+")";
            session.setUser(Long.valueOf(userId), msg.getLogin());
            stmnt.executeUpdate(sql);
            stmnt.close();
            StatusMessage statMsg = new StatusMessage();
            statMsg.setText("Successfully registred as " + msg.getLogin());
            statMsg.setType(Type.MSG_STATUS);
            session.send(statMsg);
        } catch (SQLException e) {
            System.out.println("sqlexception");
            throw new CommandException("SQLException");
        } catch (ProtocolException e) {
            throw new CommandException("ProtocolException");
        } catch (IOException e) {
            throw new CommandException("IOException");
        }

    }
}
