package arhangel.dim.container;

/**
 * Проблемы с импортом из файла конфигурации
 */

public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
    }
}
