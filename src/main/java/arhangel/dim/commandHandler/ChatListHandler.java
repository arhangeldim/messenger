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
public class ChatListHandler implements Command {
    public void execute (Session session, Message msg) throws CommandException{
        DbConnect db = new DbConnect();
        Statement stmnt;
        try {
            Connection connection = db.connect();
            stmnt = connection.createStatement();
            ResultSet rs = stmnt.executeQuery("SELECT * FROM chattousers WHERE user_id = " + msg.getSenderId().toString());
            ChatListMessage chatList = new ChatListMessage();
            chatList.setType(Type.MSG_CHAT_LIST_RESULT);
            String result = "";
            while (rs.next()) {
                result += String.valueOf(rs.getInt("chat_id")) + ';';
            }
            chatList.setChatList(result);
            session.send(chatList);
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
