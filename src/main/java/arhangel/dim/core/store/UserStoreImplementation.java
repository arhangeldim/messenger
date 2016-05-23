package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserStoreImplementation implements UserStore {
    private Connection connection;

    public UserStoreImplementation(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User getUserById(Long userId) throws StorageException {
        String sql = "SELECT login, password, id FROM users WHERE id = ?";
        User user = new User();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setName(resultSet.getString("login"));
                user.setId(resultSet.getLong("id"));
                user.setPassword(resultSet.getString("password"));
            } else {
                throw new StorageException("No such user");

            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return user;
    }
/*
    @Override
    public User getUserByUsername(String username) throws StorageException {
        String sql = "SELECT * FROM users WHERE login = ?";
        User user = null;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setName(resultSet.getString("login"));
                user.setId(resultSet.getLong("id"));
                user.setPassword(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            throw new StorageException(username);
        }
        return user;
    }
*/

    @Override
    public User addUser(User user) throws StorageException {
        String sql = "INSERT INTO users(login, password) VALUES(?, ?)";
        Long newId;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newId = generatedKeys.getLong(1);
                    user.setId(newId);
                } else {
                    throw new SQLException("failed getting new id");
                }
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(String login, String pass) throws StorageException {
        String sql = "SELECT id FROM users WHERE login = ? AND password = ?";
        User user = new User();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, pass);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
                user.setName(login);
                user.setPassword(pass);
            } else {
                throw new StorageException("No such user");
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return user;
    }
}