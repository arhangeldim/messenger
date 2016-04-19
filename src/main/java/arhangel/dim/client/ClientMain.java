package arhangel.dim.client;

import arhangel.dim.core.message.Message;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ClientMain {

    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Нить, отвечающая за получение сообщений от сервера
     */
    private Thread listenThread;

    /**
     * Нить, отвечающая за отправку сообщений на сервер
     */
    private Thread writeThread;

    /**
     * Очередь, для обмена сообщений для отправки между главной нитью и writeThread
     */
    private BlockingQueue<Message> messagesToWrite;

    /**
     * Классы, отвечающие за работу нитей
     */
    private MessageListener listener;
    private MessageWriter writer;

    public ClientMain() {
    }

    public void init() {
        try {
            //TODO: хост и порт из конфига
            int port = 9000;
            socket = new Socket("localhost", port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            messagesToWrite = new ArrayBlockingQueue<>(10);
            listener = new MessageListener(in);
            writer = new MessageWriter(out, messagesToWrite);
            listenThread = new Thread(listener);
            writeThread = new Thread(writer);
            listenThread.start();
            writeThread.start();
        } catch (Exception e) {
            System.err.println("Client initialization: exception caught: " + e.toString());
        }
    }

    public void handleUserCommands() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while(true) {
            line = reader.readLine();
            Message writeMessage;
            if (line.startsWith("/")) {
                writeMessage = new Message();
                writeMessage.setMessage(line);
                messagesToWrite.put(writeMessage);
                if (line.equals("/exit")) {
                    System.out.println("Closing..");
                    stopThreads();
                    break;
                }
            } else {
                writeMessage = new Message();
                writeMessage.setMessage(line);
                messagesToWrite.put(writeMessage);
            }
        }
    }

    public void stopThreads() {
        try {
            listenThread.interrupt();
            writeThread.interrupt();
        } catch (Exception e) {
            System.err.println("Failed to stop threads " + e.toString());
        } finally {
            IoUtil.closeQuietly(socket);
        }
    }

    public static void main(String[] args) {
        ClientMain threadedClient = new ClientMain();
        threadedClient.init();
        try {
            threadedClient.handleUserCommands();
        } catch (Exception e) {
            System.err.println("Client: exception caught: " + e.toString());
        }
    }
}
