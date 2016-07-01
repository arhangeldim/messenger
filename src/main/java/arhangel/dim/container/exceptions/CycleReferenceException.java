package arhangel.dim.container.exceptions;

/**
 * Обнаружена циклическая зависимость
 */
public class CycleReferenceException extends Exception {
    public CycleReferenceException(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}
