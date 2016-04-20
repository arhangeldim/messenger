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
public class DataBaseUserStore implements UserStore {
    /**
     * Соединение с БД
     */
    private Connection connection;

    /**
     * Исполнитель SQL-запросов
     */
    private QueryExecutor executor;

    public DataBaseUserStore(Connection conn) {
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
        return executor.execQuery(sql, queryArgs, (resSet) -> {
            List<User> data = new ArrayList<>();
            while (resSet.next()) {
                User user = new User(resSet.getString("login"),
                        resSet.getString("password"),
                        resSet.getString("nick"));

                int id = resSet.getInt("id");
                user.setId(id);
                data.add(user);
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
        return executor.execQuery(sql, queryArgs, (resSet) -> {
            User result = new User();
            while (resSet.next()) {
                result = new User(resSet.getString("login"), resSet.getString("password"), resSet.getString("nick"));
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
            result = executor.execUpdate(sql, queryArgs, (resSet) -> {
                if (resSet.next()) {
                    return resSet.getInt(1);
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
