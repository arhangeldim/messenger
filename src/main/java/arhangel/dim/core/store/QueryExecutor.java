package arhangel.dim.core.store;


import org.apache.commons.dbcp.BasicDataSource;

import javax.management.Query;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


class QueryExecutor {
    static final String PG_ADRESS = "jdbc:postgresql://178.62.140.149:5432/" +
            "thefacetakt";
    static final String USERNAME = "trackuser";
    static final String PASSWORD = "trackuser";

    private BasicDataSource ds;

    QueryExecutor() {
        ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        ds.setUrl(PG_ADRESS);
    }

    public void execQuery(String query,
                           Object[] args,
                           CheckedConsumer<ResultSet> handler)
            throws SQLException {
        try (Connection connection
                     = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            for (int i = 1; i <= args.length; ++i) {
                stmt.setObject(i, args[i - 1]);
            }
            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            handler.accept(rs);
            rs.close();
            stmt.close();
        }
    }

    public long execUpdate(String query, Object[] args)  throws SQLException {
        long result = 0;
        try (Connection connection
                     = ds.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= args.length; ++i) {
                stmt.setObject(i, args[i - 1]);
            }
            System.out.println(stmt);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            result = rs.getLong(1);
            rs.close();
            stmt.close();
        }
        return result;
    }
}
