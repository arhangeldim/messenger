package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserStoreImpl implements UserStore {
    Connection connection;

    public UserStoreImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addUser(User user) throws StorageException {
        String sql = "INSERT INTO users(name, password) VALUES(?, ?)";
        Long result;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getLong(1);
                } else {
                    throw new SQLException("failed getting new id");
                }
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        user.setId(result);
        return user;
    }

    @Override
    public List<Long> getChatListByUser(User user) throws StorageException {
        String sql = "SELECT * FROM users_chats WHERE user_id = ?";
        List<Long> resultList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, user.getId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                resultList.add(resultSet.getLong("chat_id"));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return resultList;
    }

    @Override
    public User getUser(String login) throws StorageException {
        String sql = "SELECT * FROM users WHERE name = ?";
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getLong("id"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws StorageException {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setName(resultSet.getString("name"));
                user.setId(resultSet.getLong("id"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return user;

    }
}
