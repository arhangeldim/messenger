package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class PostgresqlDaoFactory implements DaoFactory {

    private Connection connection;

    public PostgresqlDaoFactory() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/ochuikin", "trackuser", "trackuser");
        }
        return connection;
    }

    @Override
    public UserStore getUserStoreDao() throws SQLException {
        return new PostgresqlUserStore(getConnection());
    }

    @Override
    public MessageStore getMessageStoreDao() throws SQLException {
        return new PostgresqlMessageStore(this, getConnection());
    }

}
