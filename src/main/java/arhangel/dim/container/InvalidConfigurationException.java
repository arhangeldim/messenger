package arhangel.dim.container;

public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(Exception exc) {
        super(exc);
    }

    public InvalidConfigurationException(String str) {
        super(str);
    }
}
