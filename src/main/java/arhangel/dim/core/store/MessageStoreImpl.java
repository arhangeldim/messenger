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
import java.util.List;

public class MessageStoreImpl implements MessageStore {
    Connection connection;

    public MessageStoreImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) throws StorageException {
        String sql = "SELECT chat_id FROM user_chats WHERE user_id = ?";
        List<Long> resultList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
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
    public Long addChat(Long adminId, Long... participants) throws StorageException {
        String sqlChatCreate = "INSERT INTO chats(admin_id) VALUES(?)";
        Long chatId;
        try (PreparedStatement stmt = connection.prepareStatement(sqlChatCreate, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, adminId);
            stmt.executeUpdate();
            chatId = getCreatedItemId(stmt);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        String sqlParticipantsAdd = "INSERT INTO users_chats(chat_id, user_id) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlParticipantsAdd)) {
            stmt.setLong(1, chatId);
            for (Long userId : participants) {
                stmt.setLong(2, userId);
                stmt.executeUpdate();
            }
            stmt.setLong(2, adminId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return chatId;
    }

    @Override
    public List<String> getMessagesFromChat(Long chatId) throws StorageException {
        String sql = "SELECT * FROM messeges WHERE chat_id = ?";
        List<String> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                resultList.add(resultSet.getString("text"));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
    }

    @Override
    public List<Long> getChatParticipansById(Long chatId) throws StorageException {
        String sql = "SELECT * FROM users_chats WHERE chat_id = ?";
        List<Long> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                resultList.add(resultSet.getLong("user_id"));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
    }

    @Override
    public Long addMessage(Long userId, Long chatId, String text) throws StorageException {
        String sql = "INSERT INTO messeges(text, user_id, chat_id) VALUES(?, ?, ?)";
        Long result;
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, text);
            stmt.setLong(2, userId);
            stmt.setLong(3, chatId);
            stmt.executeUpdate();
            result = getCreatedItemId(stmt);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return result;
    }

    private Long getCreatedItemId(PreparedStatement statement) throws SQLException {
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
}
