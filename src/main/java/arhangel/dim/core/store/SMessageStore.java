package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class SMessageStore implements MessageStore {
    QueryExecutor qex = new QueryExecutor();

    /**
     * получаем список ид пользователей заданного чата
     */
    public List<Long> getChatsByUserId(Long userId){
        List<Long> results = new LinkedList<>();
        String stmt = "SELECT chatId FROM chats_users where UserId = " + userId;
        try {
            ResultSet result = qex.execute(stmt);
            while (result.next()) {
                results.add(result.getLong("chatId"));
            }
            return results;
        } catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * получить информацию о чате
     */
    public Chat getChatById(Long chatId) {
        Chat chat = new Chat();
        chat.setId(chatId);
        List<Long> users = new LinkedList<>();
        String stmt = "SELECT * FROM chat_users where id = " + chatId;
        try {
            ResultSet result = qex.execute(stmt);
            while (result.next()) {
                users.add(result.getLong("userId"));//??
            }
            chat.setUsers(users);
            return chat;
        }
        catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Список сообщений из чата
     */
    public List<Long> getMessagesFromChat(Long chatId) {
        List<Long> results = new LinkedList<>();
        String stmt = "SELECT * FROM messages where chatId = " + chatId;
        try {
            ResultSet result = qex.execute(stmt);
            while (result.next()) {
                results.add(result.getLong("messageId"));//??
            }
            return results;
        } catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    /**
     * Получить информацию о сообщении
     */
    public TextMessage getMessageById(Long messageId) {
        String stmt = "SELECT * FROM messages where messageId = " + messageId;
        try {
            ResultSet result = qex.execute(stmt);
            TextMessage message = new TextMessage();
            message.setSenderId(result.getLong("userId"));
            //message.setType(TE);
            message.setId(result.getLong("messageId"));
            message.setText(result.getString("Text"));
            return message;
        } catch (DataBaseException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Добавить сообщение в чат
     */
    public void addMessage(Long chatId, Message message) {
        String stmt = "INSERT INTO messages (messageId, userId, chatId) VALUES (" + message.getId() + ", "
                + message.getSenderId() + ", " + chatId + ')';
        try {
            qex.executeUpdate(stmt);
        } catch (DataBaseException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Добавить пользователя к чату
     */
    public void addUserToChat(Long userId, Long chatId) {
        String stmt = "INSERT INTO chat_users (chatId, userId) VALUES (" + chatId + ", "
                + userId + ')';
        try {
            qex.executeUpdate(stmt);
        } catch (DataBaseException ex) {
            ex.printStackTrace();
        }
    }
}
