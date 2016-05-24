package arhangel.dim.core.store;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

/**
 * Created by thefacetakt on 19.04.16.
 */
class QueryExecutor {
    static final String PG_ADRESS = "jdbc:postgresql://178.62.140.149:5432/" +
            "thefacetakt";
    static final String USERNAME = "trackuser";
    static final String PASSWORD = "trackuser";

    public void execQuery(String query,
                           Object[] args,
                           Consumer<ResultSet> handler) throws SQLException {
        try (Connection connection
                     = DriverManager.getConnection(PG_ADRESS, USERNAME,
                PASSWORD)) {
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
                     = DriverManager.getConnection(PG_ADRESS, USERNAME,
                PASSWORD)) {
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
