package arhangel.dim.core.store;


// Abstract class DAO Factory
public abstract class DaoFactory {

    // List of DAO types supported by the factory
    public enum DaoTypes {
        MySQL,
        PostgreSQL
    }

    // There will be a method for each DAO that can be
    // created. The concrete factories will have to
    // implement these methods.
    public abstract UserStore getUserDao();

    public abstract MessageStore getMessageDao();

    public static DaoFactory getDaoFactory(DaoTypes whichFactory) {

        switch (whichFactory) {
            case MySQL:
                return new MysqlDaoFactory();
            case PostgreSQL:
                return new PostgresqlDaoFactory();
            default:
                return null;
        }
    }
}