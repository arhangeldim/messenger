package arhangel.dim.core.store;

import arhangel.dim.core.authorization.User;

import java.sql.SQLException;
import java.util.List;


/**
 * Хранилище пользователей
 */
public interface UserStore {
    User getUser(int id) throws Exception;

    List<User> getUserByName(String name) throws Exception;

    int addUser(User user) throws Exception;

    void close() throws SQLException;
}
