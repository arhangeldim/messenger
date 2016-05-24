package arhangel.dim.core.store;

/**
 * Created by valeriyasin on 5/23/16.
 */
public class DataBaseException extends Exception {
    public DataBaseException(String msg) {
        super(msg);
    }

    public DataBaseException(Throwable ex) {
        super(ex);
    }
}
