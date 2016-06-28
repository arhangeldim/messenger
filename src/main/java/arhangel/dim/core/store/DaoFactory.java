package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by nv on 24.05.16.
 */
public class DaoFactory {
    private static DaoFactory daoFactory = new DaoFactory();

    private Connection connection;

    public Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/velik97",
                    "trackuser", "trackuser");
        } catch (Exception e) {
            e.getStackTrace();
        }
        return connection;
    }

    public static DaoFactory getInstance() {
        return daoFactory;
    }
}
