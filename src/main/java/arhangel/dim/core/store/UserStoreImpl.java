package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;

/**
 * Created by dmitriy on 25.04.16.
 */
public class UserStoreImpl implements UserStore {
    private Connection connection;

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
}
