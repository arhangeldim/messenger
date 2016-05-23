package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DataBase {
    public DataBase() throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/PotapovaSofia",
                "trackuser", "trackuser");
        Statement stmt;
        String sql;

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS users " +
                "(ID SERIAL PRIMARY KEY," +
                " LOGIN             TEXT    NOT NULL," +
                " PASSWORD          TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS chats " +
                "(ID SERIAL PRIMARY KEY," +
                " TEMP TEXT NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS textmessages " +
                "(TEXT_ID SERIAL PRIMARY KEY," +
                " AUTHOR_ID         INT    NOT NULL," +
                " TEXT             TEXT    NOT NULL," +
                " TEXT_DATE        TEXT     NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS chat_messages " +
                "(CHAT_ID SERIAL PRIMARY KEY," +
                " MESSAGE_ID         INT    NOT NULL," +
                " TEXT             TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();

        stmt = connection.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS chat_user " +
                "(ID SERIAL PRIMARY KEY," +
                " USER_ID           INT     NOT NULL," +
                " CHAT_ID           INT     NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();

        connection.close();
    }
}
