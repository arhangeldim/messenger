package arhangel.dim.server;

import arhangel.dim.core.authorization.Authorize;
import arhangel.dim.core.commands.Command;
import arhangel.dim.core.message.AnswerMessage;
import arhangel.dim.core.message.Message;
import arhangel.dim.core.message.Protocol;
import arhangel.dim.core.message.SerializationProtocol;
import arhangel.dim.core.session.Session;
import arhangel.dim.core.session.SessionManager;
import arhangel.dim.core.store.DataStore;

import java.io.*;
import java.util.Map;

/**
 * Обработчик команд, поступающих от определённого клиента
 */
public class CommandHandler implements Runnable{
    /**
     * Потоки для общения с клиентом
     */
    private DataInputStream reader;
    private DataOutputStream writer;

    /**
     * Список всех баз данных, требуемых для работы
     */
    private DataStore dataStore;

    /**
     * Список сессий.
     */
    private SessionManager sessionManager;

    public CommandHandler(InputStream reader, OutputStream writer, DataStore dataStore, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.reader = new DataInputStream(reader);
        this.writer = new DataOutputStream(writer);
        this.dataStore = dataStore;
    }

    @Override
    public void run() {
        try {
            Map<String, Command> commands = dataStore.getCommandsStore();
            Authorize service = new Authorize(dataStore.getUserStore());
            Session session = new Session(reader, writer, service, dataStore, sessionManager);
            sessionManager.addSession(session);
            Protocol<AnswerMessage> answerProtocol = new SerializationProtocol<>();
            Protocol<Message> readProtocol = new SerializationProtocol<>();
            while (true) {
                byte[] readData = new byte[1024 * 64];
                reader.read(readData);
                Message received = readProtocol.decode(readData);
                String command = received.getMessage();
                if (command.equals("/exit")) {
                    break;
                }
                String[] parsedCommand = command.split("\\s+");
                Command commandClass = commands.get(parsedCommand[0]);
                if (commandClass == null) {
                    String str = "Wrong command";
                    AnswerMessage answer = new AnswerMessage(str , AnswerMessage.Value.ERROR);
                    writer.write(answerProtocol.encode(answer));
                    continue;
                }
                commandClass.run(parsedCommand, session);

            }

        } catch (Exception e) {
            System.err.println("Exception in reading command " + e.toString());
        }
    }
}
