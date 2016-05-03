package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MessageStoreImplementation implements MessageStore {
    private Connection connection;

    public MessageStoreImplementation(Connection connection) {
        this.connection = connection;
    }

    private Long getNextId(PreparedStatement statement) throws SQLException {
        statement.getGeneratedKeys();
        Long result;
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                result = generatedKeys.getLong(1);
            } else {
                throw new SQLException("failed getting new id");
            }
        }
        return result;
    }

    @Override
    public Long addChat(List<Long> participants) throws StorageException {
        String sqlCreateChat = "INSERT INTO chats(temp) VALUES(?)";
        Long chatId;
        try (PreparedStatement statement = connection.prepareStatement(sqlCreateChat,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, participants.get(0));
            statement.executeUpdate();
            chatId = getNextId(statement);
            /*
             * Возвращать существующие чаты вместо создания новых
             */
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        String sqlAddParticipants = "INSERT INTO chat_user(chat_id, user_id) VALUES(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlAddParticipants)) {
            statement.setLong(1, chatId);
            for (Long userId : participants) {
                statement.setLong(2, userId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return chatId;
    }

    @Override
    public List<TextMessage> getMessagesFromChat(Long chatId) throws StorageException {
        String sql = "SELECT * FROM " +
                "chat_messages INNER JOIN textmessages " +
                "ON chat_messages.message_id = textmessages.text_id " +
                "WHERE chat_messages.chat_id = ?;";
        List<TextMessage> resultList = new LinkedList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                TextMessage message = new TextMessage(chatId, resultSet.getString("text"),
                        resultSet.getDate("text_date"));
                resultList.add(message);
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
    }

    @Override
    public Message getMessageById(Long messageId) throws StorageException {
        return null;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) throws StorageException {
        String sql = "SELECT chat_id FROM chat_user WHERE user_id = ?";
        List<Long> resultList = new LinkedList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Long chatId = result.getLong("chat_id");
                resultList.add(chatId);
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
    }

    @Override
    public Chat getChatById(Long chatId) throws StorageException {
        return null;
    }

    @Override
    public Long addMessage(Long chatId, TextMessage message) throws StorageException {
        String sql = "INSERT INTO textmessages(text, text_date, author_id) VALUES(?, ?, ?)";
        Long result;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, message.getText());
            stmt.setDate(2, new java.sql.Date(message.getDate().getTime()));
            stmt.setLong(3, message.getSenderId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new StorageException("No rows affected");
            }

            result = getNextId(stmt);

        } catch (SQLException e) {
            throw new StorageException(e);
        }

        sql = "INSERT INTO chat_messages(chat_id, message_id, text) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            stmt.setLong(2, result);
            stmt.setString(3, message.getText());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException(e);
        }

        return result;
    }

    @Override
    public void addUserToChat(Long userId, Long chatId) throws StorageException {

    }

}
