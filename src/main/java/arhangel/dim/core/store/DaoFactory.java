package arhangel.dim.core.store;


import java.sql.SQLException;

// Abstract class DAO Factory
public abstract class DaoFactory {

    // List of DAO types supported by the factory
    public enum DaoTypes {
        PostgreSQL
    }

    // There will be a method for each DAO that can be
    // created. The concrete factories will have to
    // implement these methods.
    public abstract UserStore getUserDao() throws SQLException;

    public abstract MessageStore getMessageDao() throws SQLException;

    public static DaoFactory getDaoFactory(DaoTypes whichFactory) {

        switch (whichFactory) {
            case PostgreSQL:
                return new PostgresqlDaoFactory();
            default:
                return null;
        }
    }
}