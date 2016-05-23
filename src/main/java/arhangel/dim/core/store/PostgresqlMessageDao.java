package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arhangel.dim.core.messages.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresqlMessageDao implements MessageStore {
    static Logger log = LoggerFactory.getLogger(PostgresqlMessageDao.class);

    PostgresqlDaoFactory parentFactory;

    public PostgresqlMessageDao() {
    }

    public PostgresqlMessageDao(PostgresqlDaoFactory parentFactory) throws SQLException {
        this.parentFactory = parentFactory;
        Connection connection = parentFactory.getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS messages (" +
                "id BIGSERIAL PRIMARY KEY," +
                "chat_id BIGINT REFERENCES chats (id)," +
                "sender_id BIGINT REFERENCES users (id)," +
                "text TEXT" +
                ");").execute();
        connection.close();
    }

    public void init() throws SQLException {
        log.info("[init] Initializing message dao...");
        Connection connection = parentFactory.getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS chats (" +
                "id BIGINT PRIMARY KEY NOT NULL," +
                "users BIGINT[]" +
                ");").execute();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS messages (" +
                "id BIGSERIAL PRIMARY KEY," +
                "chat_id BIGINT REFERENCES chats (id)," +
                "sender_id BIGINT REFERENCES users (id)," +
                "text TEXT" +
                ");").execute();
        connection.close();

        log.info("[init] Tables 'chats' and 'messages' now exist");
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        log.info("[getChatsByUserId] Getting chats with user {}", userId);
        List<Long> chatList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT id FROM chats WHERE users @> ARRAY[?::BIGINT];");
            selectStatement.setLong(1, userId);

            ResultSet result = selectStatement.executeQuery();

            while (result.next()) {
                chatList.add(result.getLong(1));
            }
            log.info("[getChatsByUserId] Found {} chats", chatList.size());
            return chatList;
        } catch (Exception e) {
            log.error("[getChatsByUserId] Couldn't get chats", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getChatsByUserId] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public Chat getChatById(Long chatId) {
        log.info("[getChatById] Getting chats with user {}", chatId);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM chats WHERE id = ?;");
            selectStatement.setLong(1, chatId);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                Chat chat = new Chat(result.getLong("id"),
                        new ArrayList<>(Arrays.asList((Long[]) result.getArray("users").getArray())));
                log.info("[getUserById] Got chat {}", chat.getId());
                return chat;
            }
            log.info("[getUserById] Didn't find chat {}", chatId);
            return null;
        } catch (Exception e) {
            log.error("[getChatById] Couldn't get chat", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getChatById] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        log.info("[getMessagesFromChat] Getting messages for chat {}", chatId);
        List<Long> messageList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT id FROM messages WHERE chat_id = ?;");
            selectStatement.setLong(1, chatId);

            ResultSet result = selectStatement.executeQuery();

            while (result.next()) {
                messageList.add(result.getLong("id"));
            }
            log.info("[getMessagesFromChat] Found {} messages", messageList.size());
            return messageList;
        } catch (Exception e) {
            log.error("[getMessagesFromChat] Couldn't get messages", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getMessagesFromChat] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public TextMessage getMessageById(Long messageId) {
        log.info("[getMessageById] Getting message {}", messageId);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM messages WHERE id = ?;");
            selectStatement.setLong(1, messageId);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                TextMessage message = new TextMessage();
                message.setId(result.getLong("id"));
                message.setSenderId(result.getLong("sender_id"));
                message.setType(Type.MSG_TEXT);
                message.setText(result.getString("text"));
                message.setChatId(result.getLong("chat_id"));
                log.info("[getMessageById] Got message {}", message.getId());
                return message;
            }
            log.info("[getMessageById] Didn't find message {}", messageId);
            return null;
        } catch (Exception e) {
            log.error("[getMessageById] Couldn't get message", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getMessageById] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public TextMessage addMessage(Long chatId, TextMessage message) {
        log.info("[addMessage] Adding message {} to chat {}", message.getText(), chatId);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement createStatement = connection.prepareStatement(
                    "INSERT INTO messages (chat_id, sender_id, text) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            createStatement.setLong(1, message.getChatId());
            createStatement.setLong(2, message.getSenderId());
            createStatement.setString(3, message.getText());

            int affectedRows = createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                message.setId(generatedKeys.getLong(1));
                log.info("[addMessage] Successfully added message {}", message.getId());
                return message;
            }
            log.info("[addMessage] Didn't add message {}", message.getText());
            return null;
        } catch (Exception e) {
            log.error("[addMessage] Couldn't add message", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[addMessage] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        log.info("[addUserToChat] Adding user {} to chat {}", userId, chatId);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE chats SET users = users || ?::BIGINT WHERE id = ? AND NOT users @> ARRAY[?::BIGINT];");
            updateStatement.setLong(1, userId);
            updateStatement.setLong(2, chatId);
            updateStatement.setLong(3, userId);

            int affectedRows = updateStatement.executeUpdate();
            log.info("[addUserToChat] Successfully added user to {} chats", affectedRows);
        } catch (Exception e) {
            log.error("[addUserToChat] Couldn't add user to chat", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[addUserToChat] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
    }
}
