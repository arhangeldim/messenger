package arhangel.dim.container;

/**
 * Created by dmitriy on 18.03.16.
 */

public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
    }
}
