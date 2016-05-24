package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.ds.PGPoolingDataSource;

public class PostgresqlDaoFactory extends DaoFactory {
    PGPoolingDataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public PostgresqlUserDao getUserDao() throws SQLException {
        return new PostgresqlUserDao(this);
    }

    @Override
    public PostgresqlMessageDao getMessageDao() throws SQLException {
        return new PostgresqlMessageDao(this);
    }
}