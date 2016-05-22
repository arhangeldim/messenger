package arhangel.dim.core.dbservice.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by tatiana on 17.04.16.
 */
public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;
}
