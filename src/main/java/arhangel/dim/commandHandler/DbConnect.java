package arhangel.dim.commandHandler;

import arhangel.dim.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Арина on 17.04.2016.
 */
public class DbConnect {

    public Connection connect() throws SQLException{
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/arinik2", "trackuser", "trackuser");
        } catch(SQLException e) {
            System.out.println("SQLException when connecting to db");
        }
        return connection;
    }

}
