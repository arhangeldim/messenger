package arhangel.dim.core.store;

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

    private Long getCreatedId(PreparedStatement statement) throws SQLException {
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
        String sqlChatCreate = "INSERT INTO chats(admin_id) VALUES(?)";
        Long chatId;
        try (PreparedStatement stmt = connection.prepareStatement(sqlChatCreate, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, participants.get(0));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new StorageException("No rows affected");
            }
            chatId = getCreatedId(stmt);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        String sqlParticipantsAdd = "INSERT INTO chat_user(chat_id, user_id) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlParticipantsAdd)) {
            stmt.setLong(1, chatId);
            for (Long userId : participants) {
                stmt.setLong(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return chatId;
    }

    @Override
    public List<TextMessage> getMessagesByChatId(Long chatId) throws StorageException {
        String sql = "SELECT * FROM " +
                     "chat_messages INNER JOIN textmessages " +
                     "ON chat_messages.message_id = textmessages.text_id " +
                     "WHERE chat_messages.chat_id = ?;";
        List<TextMessage> resultList = new LinkedList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                TextMessage message = new TextMessage();

                message.setDate(resultSet.getDate("text_date"));
                message.setText(resultSet.getString("text"));
                message.setChatId(chatId);

                resultList.add(message);
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
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
    public Long addTextMessage(Long chatId, TextMessage message) throws StorageException {
        //TODO chat existence check
        // Insert message
        String sql = "INSERT INTO textmessages(text, text_date) VALUES(?, ?)";
        Long result;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, message.getText());
            stmt.setDate(2, new java.sql.Date(message.getDate().getTime()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new StorageException("No rows affected");
            }

            result = getCreatedId(stmt);

        } catch (SQLException e) {
            throw new StorageException(e);
        }

        //insert message in chat
        sql = "INSERT INTO chat_messages(chat_id, message_id) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            stmt.setLong(2, result);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException(e);
        }

        return result;
    }
}
