package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresqlUserDao implements UserStore {
    static Logger log = LoggerFactory.getLogger(PostgresqlUserDao.class);

    PostgresqlDaoFactory parentFactory;

    public PostgresqlUserDao() {
    }

    public PostgresqlUserDao(PostgresqlDaoFactory parentFactory) throws SQLException {
        this.parentFactory = parentFactory;
        Connection connection = parentFactory.getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                "id BIGSERIAL PRIMARY KEY," +
                "login VARCHAR(255) UNIQUE," +
                "secret VARCHAR(255)" +
                ");").execute();
        connection.close();
    }

    public void init() throws SQLException {
        log.info("[init] Initializing user dao...");
        Connection connection = parentFactory.getConnection();
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                "id BIGSERIAL PRIMARY KEY," +
                "login VARCHAR(255) UNIQUE," +
                "secret VARCHAR(255)" +
                ");").execute();
        connection.close();

        log.info("[init] Table 'users' now exists");
    }

    @Override
    public User addUser(User user) {
        log.info("[addUser] Adding user {}", user.getLogin());
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement createStatement = connection.prepareStatement(
                    "INSERT INTO users (login, secret) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            createStatement.setString(1, user.getLogin());
            createStatement.setString(2, user.getSecret());

            int affectedRows = createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
                log.info("[addUser] User successfully added {}", user.getLogin());
                return user;
            }
            log.info("[addUser] Didn't add user");
            return null;
        } catch (Exception e) {
            log.error("[addUser] Couldn't add user", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[addUser] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        log.info("[updateUser] Updating user {}", user.getLogin());
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement updateStatement = connection.prepareStatement(
                    "UPDATE users SET login = ?, password = ? WHERE id = ?;");
            updateStatement.setString(1, user.getLogin());
            updateStatement.setString(2, user.getSecret());
            updateStatement.setLong(3, user.getId());

            int affectedRows = updateStatement.executeUpdate();

            log.info("[updateUser] User successfully updated {}", user.getLogin());
            return user;
        } catch (Exception e) {
            log.error("[updateUser] Couldn't update user", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[updateUser] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public User getUser(String login, String pass) {
        log.info("[getUser] Getting user {}:{}", login, pass);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE login = ? AND secret = ?;");
            selectStatement.setString(1, login);
            selectStatement.setString(2, pass);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));
                log.info("[getUser] Got user {}", user.getLogin());
                return user;
            }
            log.info("[getUser] Didn't find user");
            return null;
        } catch (Exception e) {
            log.error("[getUser] Couldn't get user", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getUser] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {
        log.info("[getUserById] Getting user {}", id);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE id = ?;");
            selectStatement.setLong(1, id);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));

                log.info("[getUserById] Got user {}", user.getLogin());
                return user;
            }
            log.info("[getUserById] Didn't find user {}", id);
            return null;
        } catch (Exception e) {
            log.error("[getUserById] Couldn't get user", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getUserById] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByLogin(String login) {
        log.info("[getUserByLogin] Getting user {}", login);
        Connection connection = null;
        try {
            connection = parentFactory.getConnection();
            PreparedStatement selectStatement = connection.prepareStatement(
                    "SELECT * FROM users WHERE login= ?;");
            selectStatement.setString(1, login);

            ResultSet result = selectStatement.executeQuery();

            if (result.next()) {
                User user = new User(result.getString("login"), result.getString("secret"));
                user.setId(result.getLong(1));
                log.info("[getUserByLogin] Got user {}", user.getLogin());
                return user;
            }
            log.info("[getUserByLogin] Didn't get user {}", login);
            return null;
        } catch (Exception e) {
            log.error("[getUserByLogin] Couldn't get user", e);
            //e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("[getUserByLogin] Couldn't close connection", e);
                    //e.printStackTrace();
                }
            }
        }
        return null;
    }
}
