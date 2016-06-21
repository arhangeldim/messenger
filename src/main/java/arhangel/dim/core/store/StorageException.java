package arhangel.dim.core.store;

import java.sql.SQLException;

public class StorageException extends Exception {
    public StorageException(SQLException e) {
        super(e);
    }
}
