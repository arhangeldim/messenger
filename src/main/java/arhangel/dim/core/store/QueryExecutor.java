package arhangel.dim.core.store;

import java.sql.*;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class QueryExecutor {
    public ResultSet execute(String command) throws DataBaseException {
        Connection connection = null;
        String url = "jdbc:postgresql://127.0.0.1:5432/test";
        String name = "user";
        String password = "123456";
        try {
            Class.forName("org.postgresql.Driver");
            //System.out.println("Драйвер подключен");
            connection = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение установлено");
            Statement statement = null;

            statement = connection.createStatement();
            ResultSet result = statement.executeQuery(command);
            return result;
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DataBaseException(ex.getMessage());
        }
    }

    public void executeUpdate(String command)throws DataBaseException {
        Connection connection = null;
        String url = "jdbc:postgresql://127.0.0.1:5432/test";
        String name = "user";
        String password = "123456";
        try {
            Class.forName("org.postgresql.Driver");
            //System.out.println("Драйвер подключен");
            connection = DriverManager.getConnection(url, name, password);
            //System.out.println("Соединение установлено");
            Statement statement = null;

            statement = connection.createStatement();
            statement.executeUpdate(command);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new DataBaseException(ex.getMessage());
        }
    }
}
