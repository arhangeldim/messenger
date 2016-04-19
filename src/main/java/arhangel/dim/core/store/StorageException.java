package arhangel.dim.core.store;

import java.sql.SQLException;

public class StorageException extends Exception {
    public StorageException(SQLException exception) {
        super(exception);
    }

    public StorageException(String exception) {
        super(exception);
    }
}
