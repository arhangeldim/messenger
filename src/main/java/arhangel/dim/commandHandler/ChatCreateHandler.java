package arhangel.dim.commandHandler;

import arhangel.dim.DbConnect;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.Session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Арина on 17.04.2016.
 */
public class ChatCreateHandler implements Command {
        public void execute(Session session, Message message) throws CommandException {
            ChatCreateMessage msg = (ChatCreateMessage) message;
            DbConnect db = new DbConnect();
            Statement stmnt;
            int chatId = 0;
            try {
                Connection connection = db.connect();
                stmnt = connection.createStatement();
                ResultSet rs = stmnt.executeQuery("SELECT MAX(chat_id) as max_id FROM chattouser");
                if (rs.next()) {
                    chatId = rs.getInt("max_id") + 1;
                }
            } catch (SQLException e) {
                CommandException ex = new CommandException("SQLException");
                throw ex;
            }

            String[] userList = msg.getUserList();
            try {
                for (int i = 0; i < userList.length; i++) {

                    String sql = "INSERT INTO chattouser VALUES ("+Integer.toString(chatId)+","+userList[i]+")";
                    stmnt.executeUpdate(sql);
                }
                stmnt.close();
            } catch (SQLException e) {
                CommandException ex = new CommandException("SQLException");
                throw ex;
            }


    }
}
