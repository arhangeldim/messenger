package arhangel.dim.container;

/**
 * Обнаружена циклическая зависимость
 */
public class CycleReferenceException extends InvalidConfigurationException {
    public CycleReferenceException(String message) {
        super(message);
    }
}
