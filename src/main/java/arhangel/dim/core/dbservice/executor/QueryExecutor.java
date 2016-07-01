package arhangel.dim.core.dbservice.executor;

import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class QueryExecutor {
    static org.slf4j.Logger log = LoggerFactory.getLogger(QueryExecutor.class);
    Connection connection;

    private Map<String, PreparedStatement> execPrepareds = new HashMap<>();
    private Map<String, PreparedStatement> updatePrepareds = new HashMap<>();
    private Map<String, PreparedStatement> updateWithKeysPrepareds = new HashMap<>();

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public <T> T execQuery(String query, Map<Integer, Object> args, ResultHandler<T> handler) throws SQLException {

        PreparedStatement stmt;
        if (execPrepareds.containsKey(query)) {
            stmt = execPrepareds.get(query);
        } else {
            stmt = connection.prepareStatement(query);
            execPrepareds.put(query, stmt);
        }

        stmt.clearParameters();

        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        ResultSet resultset = stmt.executeQuery();
        T value = handler.handle(resultset);
        resultset.close();
        return value;
    }

    public void updateQuery(String query) throws SQLException {

        Statement stmt = connection.createStatement();
        stmt.execute(query);
        stmt.close();

    }

    public void updateQuery(String query, Map<Integer, Object> args) throws SQLException {

        PreparedStatement stmt;
        if (updatePrepareds.containsKey(query)) {
            stmt = updatePrepareds.get(query);
        } else {
            stmt = connection.prepareStatement(query);
            updatePrepareds.put(query, stmt);
        }

        stmt.clearParameters();

        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        stmt.executeQuery();

    }

    public Long updateQueryWithGeneratedKey(String query, Map<Integer, Object> args, String keyName) throws SQLException {

        PreparedStatement stmt;
        if (updateWithKeysPrepareds.containsKey(query)) {
            stmt = updateWithKeysPrepareds.get(query);
        } else {
            stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            updateWithKeysPrepareds.put(query, stmt);
        }

        stmt.clearParameters();

        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }

        stmt.executeUpdate();

        ResultSet resultset = stmt.getGeneratedKeys();
        resultset.next();
        Long key = resultset.getLong(keyName);

        resultset.close();

        return key;
    }

    public void close() {
        try {
            for (Map.Entry<String, PreparedStatement> entry : execPrepareds.entrySet()) {
                entry.getValue().close();
            }
            for (Map.Entry<String, PreparedStatement> entry : updatePrepareds.entrySet()) {
                entry.getValue().close();
            }
            for (Map.Entry<String, PreparedStatement> entry : updateWithKeysPrepareds.entrySet()) {
                entry.getValue().close();
            }
        } catch (SQLException sqlExc) {
            log.error("Ошибка закрытия sql", sqlExc);
        }
    }
}
