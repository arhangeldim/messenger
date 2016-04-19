package arhangel.dim.core.message;

import java.io.Serializable;

/**
 * Ответ сервера на запрос клиента
 */
public class AnswerMessage implements Serializable {

    public enum Value {
        SUCCESS,
        ERROR,
        LOGIN,
        NUM_ARGS,
        CHAT,
    }

    private int id;
    private String from;
    private String message;
    private Value result;

    public AnswerMessage(String message, Value result) {
        this.message = message;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Value getResult() {
        return result;
    }

    public void setResult(Value result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
