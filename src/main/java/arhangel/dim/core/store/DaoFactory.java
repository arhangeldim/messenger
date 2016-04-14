package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by philip on 13.04.16.
 */
public class DaoFactory {
    private static DaoFactory daoFactory = new DaoFactory();

    Connection conn;

    public Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/PhilSk", "trackuser", "trackuser");
        } catch (Exception e) {
            e.getMessage();
        }
        return conn;
    }

    public void disconnect() {
        try {
            if (conn.isClosed()) {
                System.out.println("connection is already closed");
            } else {
                conn.close();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public static DaoFactory getInstance() {
        return daoFactory;
    }

    public UserDao getUserDao() {
        return new UserDao();
    }

}



