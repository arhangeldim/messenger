package arhangel.dim.container;

<<<<<<< HEAD
public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException() {
=======
/**
 * Неверная конфигурация
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
>>>>>>> 38695ca70e9ef2370d579b5b30f8efebc438659d
    }
}
