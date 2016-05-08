package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.ChatMessage;
import arhangel.dim.core.messages.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmitriy on 25.04.16.
 */
public class MessageStoreImpl implements MessageStore {
    private Connection connection;
    private Logger log = LoggerFactory.getLogger(MessageStoreImpl.class);

    public MessageStoreImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        String sql = "SELECT chat_id FROM Chat_User WHERE user_id = ?";
        List<Long> chatList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Long chatId = result.getLong("chat_id");
                chatList.add(chatId);
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsByUserId");
            e.printStackTrace();
        }
        return chatList;
    }

    @Override
    public Chat getChatById(Long chatId) {
        String sql = "SELECT * FROM Chat WHERE Chat.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                Chat res = new Chat();
                res.setId(chatId);
                res.setMessages(getMessagesFromChat(chatId));
                UserStoreImpl userStore = new UserStoreImpl(connection);
                res.setUsers(userStore.getUsersByChatId(chatId));
                return res;
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getChatById");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        String sql = "SELECT id FROM Message WHERE chat_id = ?";
        List<Long> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                resultList.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getMessagesFromChat");
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public Message getMessageById(Long messageId) {
        String sql = "SELECT chat_id, text FROM Message WHERE Message.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, messageId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new ChatMessage(resultSet.getLong("chat_id"), resultSet.getString("text"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getMessagesFromChat");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addMessage(Long chatId, Message message) {
        String sql = "INSERT INTO Message(text, chat_id, user_id) VALUES(?, ?, ?)";

        if (message.getType() == Type.MSG_TEXT) {
            ChatMessage msg = (ChatMessage) message;
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, msg.getText());
                stmt.setLong(2, chatId);
                stmt.setLong(3, msg.getSenderId());
                Integer affectedRows = stmt.executeUpdate();
                log.info("Added" + affectedRows.toString() + " rows to Message");

            } catch (SQLException e) {
                log.error("Caught SQLException in addMessage");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        String sql = "INSERT INTO Chat_User(chat_id, user_id) VALUES(?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            stmt.setLong(2, userId);
            Integer affectedRows = stmt.executeUpdate();
            log.info("Added" + affectedRows.toString() + " rows to Chat_User");

        } catch (SQLException e) {
            log.error("Caught SQLException in addUserToChat");
            e.printStackTrace();
        }
    }
}
