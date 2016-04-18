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
    public String getUserInformation(Long userId) throws StorageException {
        String sql = "SELECT login FROM users WHERE id = ?";
        String result;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString("login");
            } else {
                throw new StorageException("No such user");
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return result;
    }

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

    @Override
    public Long addUser(User user) throws StorageException {
        String sql = "INSERT INTO users(login, password) VALUES(?, ?)";
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
        return result;
    }
}
