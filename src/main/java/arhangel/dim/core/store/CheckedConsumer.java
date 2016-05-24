package arhangel.dim.core.store;

import java.sql.SQLException;

/**
 * Created by thefacetakt on 19.04.16.
 */

@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T arg) throws SQLException;
}
