package arhangel.dim.container;

/**
 * Неверная конфигурация
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
    }
}
