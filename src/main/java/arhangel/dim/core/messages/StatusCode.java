package arhangel.dim.core.messages;

/**
 * Created by dmitriy on 27.04.16.
 * Коды статуса, нужны для обратной связи сервера и клиента
 */
public enum StatusCode {
    OK,
    UnknownCommand,
    LoggingInSucceed,
    LoggingInFailed
}
