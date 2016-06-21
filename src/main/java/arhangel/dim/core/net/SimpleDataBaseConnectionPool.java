package arhangel.dim.core.net;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class SimpleDataBaseConnectionPool {
    private LinkedList<Connection> connections;
    private int poolSize;

    public SimpleDataBaseConnectionPool(int poolSize, String url, String name, String password) throws SQLException {
        this.poolSize = poolSize;
        this.connections = new LinkedList<>();
        for (int i = 0; i < poolSize; i++) {
            connections.push(DriverManager.getConnection(url, name, password));
        }
    }

    public  Connection getConnection() throws ConnectionPoolException {
        if (connections.isEmpty()) {
            throw new ConnectionPoolException("Connection pool is empty");
        }
        Connection connection = connections.pop();
        return connection;
    }

    public void putConnection(Connection connection) {
        connections.push(connection);
    }
}
