package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.SQLException;

/**
 * Created by thefacetakt on 19.04.16.
 */
public class PgUserStore implements UserStore {
    QueryExecutor executor;

    @Override
    public User addUser(User user) {
        Object[] args = {user.getName(), user.getPassword()};
        try {
            user.setId(executor.execUpdate("INSERT INTO USERS (login, " +
                    "password)" +
                    "VALUES(?, ?)", args));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        Object[] args = {user.getName(), user.getPassword()};
        try {
            executor.execQuery("UPDATE USERS SET LOGIN=?, " +
                            "PASSWORD=? WHERE ID=?",
                    args, rs -> { } );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    @Override
    public User getUser(String login, String pass) {
        final User[] result = {new User()};
        result[0].setName(login);
        result[0].setPassword(pass);
        Object[] args = {login, pass};
        try {
            executor.execQuery("SELECT ID FROM USERS WHERE LOGIN=? AND PASSWORD=?",
                    args, rs -> {
                        try {
                            if (rs.next()) {
                                result[0].setId(rs.getLong("ID"));
                            } else {
                                result[0] = null;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            result[0] = null;
                        }
                    } );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result[0];
    }

    @Override
    public User getUserById(Long id) {
        final User[] result = {new User()};
        result[0].setId(id);
        Object[] args = {id};
        try {
            executor.execQuery("SELECT LOGIN, PASSWORD FROM USERS WHERE ID=?",
                    args, rs -> {
                        try {
                            if (rs.next()) {
                                result[0].setPassword(rs.getString("PASSWORD"));
                                result[0].setName(rs.getString("LOGIN"));
                            } else {
                                result[0] = null;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            result[0] = null;
                        }
                    } );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return result[0];
    }

    public PgUserStore() {
        executor = new QueryExecutor();
    }
}
