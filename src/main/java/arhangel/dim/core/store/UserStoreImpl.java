package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dmitriy on 25.04.16.
 */
public class UserStoreImpl implements UserStore {
    private Connection connection;
    private Logger log = LoggerFactory.getLogger(UserStoreImpl.class);

    public UserStoreImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO User(login, password) VALUES(?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long newId = generatedKeys.getLong("id");
                    user.setId(newId);
                } else {
                    throw new SQLException("Couldn't get the id of the new user");
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in addUser");
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE User SET login = ?, password = ? WHERE User.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());
            Integer affected = stmt.executeUpdate();
            log.info("Affected" + affected.toString() + " rows in table User");
            if (affected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in updateUser");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public User getUser(String login, String pass) {
        String sql = "SELECT id FROM User WHERE login = ? AND password = ?";
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
                return null;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getUser");
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT login,password FROM User WHERE id = ?";
        User user = new User();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setName(resultSet.getString("login"));
                user.setId(id);
                user.setPassword(resultSet.getString("password"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getUserById");
            e.printStackTrace();
        }
        return user;
    }

    public List<Long> getUsersByChatId(Long chatId) {
        String sql = "SELECT user_id FROM Chat_User WHERE chat_id = ?";
        List<Long> userList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, chatId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                Long userId = result.getLong("user_id");
                userList.add(userId);
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getUsersByChatId");
            e.printStackTrace();
        }
        return userList;
    }
}
