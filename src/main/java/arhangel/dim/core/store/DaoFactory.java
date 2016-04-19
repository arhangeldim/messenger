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
    public abstract UserStore getUserDAO();

    public abstract MessageStore getMessageDAO();

    public static DaoFactory getDAOFactory(DaoTypes whichFactory) {

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