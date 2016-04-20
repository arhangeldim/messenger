package arhangel.dim.core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для отправки запросов в БД
 */
public class QueryExecutor {
    /**
     * Словарь подготовленных запросов
     */
    private Map<String, PreparedStatement> preparedStatementMap;

    /**
     * Словарь подготовленных запросов, с Generated Keys
     */
    private Map<String, PreparedStatement> preparedStatementMapGenKey;

    public QueryExecutor() {
        preparedStatementMap = new HashMap<>();
        preparedStatementMapGenKey = new HashMap<>();
    }

    public void prepareStatement(Connection connection, String query) throws SQLException {
        if (preparedStatementMap.get(query) == null) {
            preparedStatementMap.put(query, connection.prepareStatement(query));
        }
    }

    public void prepareStatementGeneratedKeys(Connection connection, String query) throws SQLException {
        if (preparedStatementMapGenKey.get(query) == null) {
            preparedStatementMapGenKey.put(query, connection.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS));
        }
    }

    public <T> T execQuery(Connection connection, String query, ResultHandler<T> handler) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        T value = handler.handle(result);
        result.close();
        stmt.close();

        return value;
    }

    public <T> T execQuery(String sql, Map<Integer, Object> args, ResultHandler<T> handler) throws SQLException {
        PreparedStatement pstmt = preparedStatementMap.get(sql);
        if (pstmt == null) {
            throw new SQLException(String.format("Statement \"%s\" wasn't prepared", sql));
        }
        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            pstmt.setObject(entry.getKey(), entry.getValue());
        }
        ResultSet rs = pstmt.executeQuery();
        T value = handler.handle(rs);
        rs.close();
        return value;
    }

    public void execUpdate(String sql, Map<Integer, Object> args) throws SQLException {
        PreparedStatement pstmt = preparedStatementMap.get(sql);
        if (pstmt == null) {
            throw new SQLException(String.format("Statement \"%s\" wasn't prepared", sql));
        }
        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            pstmt.setObject(entry.getKey(), entry.getValue());
        }
        pstmt.executeUpdate();
    }

    public <T> T execUpdate(String sql, Map<Integer, Object> args, ResultHandler<T> handler) throws SQLException {
        PreparedStatement pstmt = preparedStatementMapGenKey.get(sql);
        if (pstmt == null) {
            throw new SQLException(String.format("Statement \"%s\" wasn't prepared", sql));
        }
        for (Map.Entry<Integer, Object> entry : args.entrySet()) {
            pstmt.setObject(entry.getKey(), entry.getValue());
        }
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        T value = handler.handle(rs);
        rs.close();
        return value;
    }

    public void close() throws SQLException {
        for (Map.Entry<String, PreparedStatement> pair : preparedStatementMap.entrySet()) {
            pair.getValue().close();
        }
        for (Map.Entry<String, PreparedStatement> pair : preparedStatementMapGenKey.entrySet()) {
            pair.getValue().close();
        }
    }
}
