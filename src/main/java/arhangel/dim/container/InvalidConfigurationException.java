package arhangel.dim.container;

/**
 * Created by riv on 18.03.2016.
 * *Если не получается считать конфиг, то бросается исключение
 * InvalidConfigurationException неверный конфиг
 */
public class InvalidConfigurationException extends Exception {
    public InvalidConfigurationException(String message) {
        super(message);
    }
}
