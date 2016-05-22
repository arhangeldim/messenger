package arhangel.dim.core.store;


import java.sql.Connection;

public class MysqlDaoFactory extends DaoFactory {
    public static final String DRIVER =
            "com.mysql.jdbc.Driver";
    public static final String DBURL =
            "jdbc:mysql://192.168.1.136:3306";

    // method to create Cloudscape connections
    public static Connection createConnection() {
        // Use DRIVER and DBURL to create a connection
        // Recommend connection pool implementation/usage
        return null;
    }

    @Override
    public MysqlUserDao getUserDao() {
        return new MysqlUserDao();
    }

    @Override
    public MysqlMessageDao getMessageDao() {
        return new MysqlMessageDao();
    }
}
