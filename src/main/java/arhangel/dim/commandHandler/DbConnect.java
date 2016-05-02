package arhangel.dim.commandhandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Арина on 17.04.2016.
 */
public class DbConnect {

    public Connection connect() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://178.62.140.149:5432/arinik2";
            connection = DriverManager.getConnection(url, "trackuser", "trackuser");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SQLException when connecting to db");
        }
        return connection;
    }

    public static void main(String[] args) throws Exception {
        DbConnect db = new DbConnect();
        db.connect();
    }
}
