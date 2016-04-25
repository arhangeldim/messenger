package arhangel.dim.core.store;

import arhangel.dim.container.Bean;
import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

/**
 * Created by dmitriy on 25.04.16.
 */
public class Db {
    public Db() throws Exception {
        Class.forName("org.postgresql.Driver");
        try {
            Container container = new Container("server.xml");
            Server server = (Server) container.getByClass("arhangel.dim.server.Server");
            Connection connection = DriverManager.getConnection(server.getDbLoc(), server.getDbLogin(),
                    server.getDbPassword());
            Statement stmt;
            String sql;

            stmt = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS User " +
                    "(id SERIAL PRIMARY KEY," +
                    " login             VARCHAR(255)    NOT NULL," +
                    " password          VARCHAR(255)    NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS Chat " +
                    "(id SERIAL PRIMARY KEY," +
                    " owner_id SERIAL references User(id))";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS Message " +
                    "(id SERIAL PRIMARY KEY," +
                    " text         TEXT    NOT NULL," +
                    " timestamp             TIMESTAMP    NOT NULL," +
                    " chat_id SERIAL references Chat(id)" +
                    " user_id SERIAL references User(id))";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = connection.createStatement();
            sql = "CREATE TABLE IF NOT EXISTS chat_user " +
                    "(chat_id SERIAL references Chat(id)" +
                    " user_id SERIAL references User(id))";
            stmt.executeUpdate(sql);
            stmt.close();

            connection.close();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
