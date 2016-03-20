package arhangel.dim.container;

/**
 * Обнаружена циклическая зависимость
 */
public class InvalidReferenceException extends InvalidConfigurationException {
    public InvalidReferenceException(String message) {
        super(message);
    }
}
