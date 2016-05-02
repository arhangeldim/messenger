package arhangel.dim.commandhandler;

import arhangel.dim.core.messages.ChatCreateMessage;
import arhangel.dim.core.messages.StatusMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.messages.Command;
import arhangel.dim.core.messages.CommandException;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ChatCreateHandler extends CommandHandler implements Command {
    public void execute(Session session, Message message) throws CommandException {
        if (session.getUser() == null) {
            session.notLoggedIn("Unlogged users cannot create chats. Your chat isn't created. Log in to create chats.");
            return;
        }
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
                ResultSet rs = stmnt.executeQuery("SELECT * FROM users WHERE user_id = " + userList[i]);
                if (ifErrorSend(rs.next(), session, "There is no user with id = " + userList[i])) {
                    return;
                }
            }
            for (int i = 0; i < userList.length; i++) {
                String sql = "INSERT INTO chattouser VALUES (" + Integer.toString(chatId) + "," + userList[i] + ")";
                stmnt.executeUpdate(sql);
            }
            stmnt.close();
        } catch (SQLException e) {
            CommandException ex = new CommandException("SQLException");
            throw ex;
        }
        StatusMessage statMes = new StatusMessage();
        statMes.setText("Chat with users " + userList.toString() + " was created. Chat id " + Integer.toString(chatId));
        statMes.setType(Type.MSG_STATUS);
        try {
            session.send(statMes);
        } catch (IOException e) {
            CommandException ex = new CommandException("IOException");
            throw ex;
        } catch (ProtocolException e) {
            CommandException ex = new CommandException("ProtocolException");
            throw ex;
        }

    }
}
