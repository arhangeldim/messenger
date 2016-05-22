package arhangel.dim.container;

/**
<<<<<<< HEAD
 * Created by valeriyasin on 3/15/16.
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String message) {
        super(message);
=======
 * Неверная конфигурация
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String msg) {
        super(msg);
    }

    public InvalidConfigurationException(Throwable ex) {
        super(ex);
>>>>>>> 5044e64aedcc627f70c5d919734be1e8583b899e
    }
}
