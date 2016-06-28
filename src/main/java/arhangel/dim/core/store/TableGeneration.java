package arhangel.dim.core.store;

import arhangel.dim.core.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by nv on 24.05.16.
 */
public class TableGeneration {
    static DaoFactory daoFactory = DaoFactory.getInstance();

    public static void generate() {
        try {
            Connection connection = daoFactory.connect();
            Statement statement = connection.createStatement();

            String query;

            query = "CREATE TABLE IF NOT EXISTS USERS" +
                    "(ID                BIGSERIAL PRIMARY KEY      NOT NULL," +
                    " LOGIN             TEXT                    NOT NULL, " +
                    " PASSWORD          TEXT                    NOT NULL);";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS MESSAGES" +
                    "(MSG_ID            BIGSERIAL PRIMARY KEY      NOT NULL," +
                    " USER_ID           BIGINT                  NOT NULL, " +
                    " TEXT              TEXT                    NOT NULL," +
                    " CHAT_ID           BIGINT                  NOT NULL);";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS CHATS" +
                    "(CHAT_ID           BIGSERIAL PRIMARY KEY      NOT NULL," +
                    " ADMIN_ID          BIGINT                  NOT NULL)";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS USER_GROUPS" +
                    "(CHAT_ID           BIGINT                  NOT NULL," +
                    " USER_ID           BIGINT                  NOT NULL)";
            statement.executeUpdate(query);
            statement.close();

            connection.setAutoCommit(false);
            connection.commit();

            connection.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        generate();
    }

}
