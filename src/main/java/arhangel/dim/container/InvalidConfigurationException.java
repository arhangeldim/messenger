package arhangel.dim.container;

public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
    }
}
