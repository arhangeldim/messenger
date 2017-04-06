package ivanov.mikhail.lections.objects;

/**
 *
 */
public abstract class AServer {

    protected abstract boolean validate(String[] params);

    protected abstract void processContent();

    protected String onError() {
        return "Failed to parse request";
    }

    public void doGet(String request) {
        String[] parameters = parseParameters(request);
        if (validate(parameters)) {
            processContent();
        } else {
            onError();
        }
    }

    private String[] parseParameters(String request) {
        // разбираем строку параметров
        return null;
    }
}
