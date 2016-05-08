package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User getUser(String login, String pass) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
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
