package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by dmitriy on 25.04.16.
 */
public class Db {
    public Db(String dbLoc, String dbLogin, String dbPass) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(dbLoc, dbLogin, dbPass);
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
                "(id      SERIAL PRIMARY KEY," +
                " text    TEXT    NOT NULL," +
                " chat_id SERIAL references Chat(id)," +
                " user_id SERIAL references User(id)," +
                " timestamp TIMESTAMP NOT NULL DEFAULT current_timestamp)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS Chat_User " +
                "(chat_id SERIAL references Chat(id)" +
                " user_id SERIAL references User(id))";
        stmt.executeUpdate(sql);
        stmt.close();

        connection.close();
    }
}
