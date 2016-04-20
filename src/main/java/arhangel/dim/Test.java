package arhangel.dim;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by thefacetakt on 19.04.16.
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException,
            SQLException {
        Class.forName("org.postgresql.Driver");
        try (Connection connection
                    = DriverManager.getConnection("jdbc:postgresql://"
                                                  + "178.62.140.149:5432/"
                                                  + "thefacetakt",
                                                  "trackuser", "trackuser")) {

            DatabaseMetaData md = connection.getMetaData();
            try (ResultSet rs = md.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    System.out.println(rs.getString(3));
                }
            }
        }


    }

}
