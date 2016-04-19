package arhangel.dim.client;

import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;

import java.io.DataInputStream;

/**
 * Класс, получающий сообщения от сервера
 */
public class MessageListener implements Runnable {
    private DataInputStream reader;

    /**
     * Протокол кодирования
     */
    private Protocol<AnswerMessage> readProtocol;

    public MessageListener(DataInputStream reader) {
        this.reader = reader;
        readProtocol = new SerializationProtocol<>();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] buf = new byte[1024 * 60];
                int readSize = reader.read(buf);
                if (readSize > 0) {
                    AnswerMessage answer = readProtocol.decode(buf);
                    handleAnswer(answer);
                }
            }
        } catch (Exception e) {
            System.err.println("Listener: exception caught: " + e.toString());
        }
    }

    public void handleAnswer(AnswerMessage answer) throws Exception {
        AnswerMessage.Value type = answer.getResult();
        String message = answer.getMessage();
        switch (type) {
            case SUCCESS:
                System.out.println(message);
                break;
            case ERROR:
                System.out.println("Error:");
                System.out.println(message);
                break;
            case LOGIN:
                System.out.println("Error:\nPlease login first");
                break;
            case NUM_ARGS:
                System.out.println("Error:\nWrong number of arguments");
                break;
            case CHAT:
                System.out.println(message);
                break;
        }
    }
}
