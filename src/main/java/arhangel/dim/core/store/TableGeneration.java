package arhangel.dim.core.store;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by philip on 13.04.16.
 */
public class TableGeneration {
    static DaoFactory daoFactory =  DaoFactory.getInstance();

    public static void generate() {
        try {
            Connection conn = daoFactory.connect();

            Statement statement = conn.createStatement();
            String query;

            query = "CREATE TABLE IF NOT EXISTS USER" +
                    "(ID             INT PRIMARY KEY     NOT NULL," +
                    " LOGIN          TEXT                NOT NULL, " +
                    " PASSWORD       BIGINT                NOT NULL);";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS MESSAGES" +
                    "(MSG_ID           INT PRIMARY KEY     NOT NULL," +
                    " USER_ID          BIGINT                NOT NULL, " +
                    " TEXT             TEXT                NOT NULL," +
                    " CHAT_ID          BIGINT                NOT NULL);";
            statement.executeUpdate(query);

            query = "CREATE TABLE IF NOT EXISTS USER_CHAT" +
                    "(CHAT_ID         BIGINT                NOT NULL," +
                    "USER_ID          BIGINT                NOT NULL," +
                    "PRIMARY KEY (CHAT_ID, USER_ID));";
            statement.executeUpdate(query);
            statement.close();

            conn.setAutoCommit(false);
            conn.commit();

        } catch (Exception e) {
            e.getMessage();
        }
    }
}
