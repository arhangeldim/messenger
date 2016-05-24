package arhangel.dim.container;

/**
 * Created by olegchuikin on 18/03/16.
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException() {
        super();
    }
}
