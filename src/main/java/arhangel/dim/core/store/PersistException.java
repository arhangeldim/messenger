package arhangel.dim.core.store;

/**
 * Created by olegchuikin on 19/04/16.
 */
public class PersistException extends Exception {
    public PersistException(String exception) {
        super(exception);
    }

    public PersistException(Exception exception) {
        super(exception);
    }
}
