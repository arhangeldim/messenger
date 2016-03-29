package arhangel.dim.container;

/**
 * Обнаружена циклическая зависимость
 */
public class CycleReferenceException extends Exception {
    public CycleReferenceException(String message) {
        super(message);
    }

    public CycleReferenceException() {
        super();
    }
}
