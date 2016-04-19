package arhangel.dim.core.store;

import arhangel.dim.core.authorization.User;
import arhangel.dim.core.jdbc.QueryExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище пользователей, реализованное на базе данных PostegreSql.
 */
public class DBUserStore implements UserStore {
    /**
     * Соединение с БД
     */
    private Connection connection;

    /**
     * Исполнитель SQL-запросов
     */
    private QueryExecutor executor;

    public DBUserStore(Connection conn) {
        this.connection = conn;
        this.executor = new QueryExecutor();
    }

    @Override
    public synchronized List<User> getUserByName(String name) throws Exception {
        if (name == null) {
            return null;
        }
        Map<Integer, Object> queryArgs = new HashMap<>();
        queryArgs.put(1, name);
        String sql = "SELECT * FROM USERS where LOGIN = ?";
        executor.prepareStatement(connection, sql);
        return executor.execQuery(sql, queryArgs, (r) -> {
            List<User> data = new ArrayList<>();
            while (r.next()) {
                User u = new User(r.getString("login"), r.getString("password"), r.getString("nick"));
                int id = r.getInt("id");
                u.setId(id);
                data.add(u);
            }
            return data;
        });
    }

    @Override
    public synchronized User getUser(int id) throws Exception {
        if (id < 0) {
            return null;
        }
        Map<Integer, Object> queryArgs = new HashMap<>();
        queryArgs.put(1, id);
        String sql = "SELECT * FROM USERS where ID = ?";
        executor.prepareStatement(connection, sql);
        return executor.execQuery(sql, queryArgs, (r) -> {
            User result = new User();
            while (r.next()) {
                result = new User(r.getString("login"), r.getString("password"), r.getString("nick"));
                result.setId(id);
            }
            return result;
        });
    }

    @Override
    public synchronized int addUser(User user) throws Exception {
        if (user == null) {
            System.out.println("Can't add user");
            return -1;
        }
        int result = -1;
        try {
            Map<Integer, Object> queryArgs = new HashMap<>();
            queryArgs.put(1, user.getName());
            queryArgs.put(2, Integer.toString(user.getPassword().hashCode()));
            queryArgs.put(3, user.getName());
            String sql = "INSERT INTO USERS (LOGIN, PASSWORD, NICK) VALUES (?, ?, ? )";
            executor.prepareStatementGeneratedKeys(connection, sql);
            result = executor.execUpdate(sql, queryArgs, (r) -> {
                if (r.next()) {
                    return r.getInt(1);
                }
                return -1;
            });
        } catch (Exception e) {
            System.err.println("UserStore: failed to write data " + e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public void close() throws SQLException {
        executor.close();
    }
}
