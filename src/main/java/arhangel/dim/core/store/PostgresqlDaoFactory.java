package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresqlDaoFactory extends DaoFactory {
    public static final String DRIVER =
            "org.postgresql.Driver";
    public static final String DBURL =
            "jdbc:postgresql://178.62.140.149:5432/gafusss";

    // method to create Cloudscape connections
    public static Connection createConnection() {
        // Use DRIVER and DBURL to create a connection
        // Recommend connection pool implementation/usage
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(DBURL, "trackuser", "trackuser");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public PostgresqlUserDao getUserDao() {
        return new PostgresqlUserDao();
    }

    @Override
    public PostgresqlMessageDao getMessageDao() {
        return new PostgresqlMessageDao();
    }
}