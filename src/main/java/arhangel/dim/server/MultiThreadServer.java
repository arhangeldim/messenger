package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.beans.WebConnection;
import arhangel.dim.core.store.DataBaseUserStore;
import arhangel.dim.core.jdbc.DataBaseOrganizer;
import arhangel.dim.core.store.DataBaseChatStore;
import arhangel.dim.core.session.SessionManager;
import arhangel.dim.core.store.DataStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Многопоточный сервер
 */
public class MultiThreadServer implements Runnable, AutoCloseable {

    private int serverPort;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private Connection connection;
    private int maxConnection = 16;

    /**
     * Нити, для работы с отдельным клиентом
     */
    private List<Thread> clientThreads;

    private DataStore dataStore;

    /**
     * Все сессии клиентов
     */
    private SessionManager sessionManager;


    public MultiThreadServer() {
        try {
            Container container = new Container("C:\\Users\\Дмитрий\\Documents\\technotrack\\java\\messenger\\config.xml");
            WebConnection portAndHost = (WebConnection)container.getByClass("arhangel.dim.container.beans.WebConnection");
            this.serverPort = portAndHost.getPort();
            isStopped = false;
            clientThreads = new ArrayList<>();
            DataBaseOrganizer.reorganizeDataBase(null);
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kud8",
                    "trackuser", "trackuser");
            dataStore = new DataStore(new DataBaseUserStore(connection), new DataBaseChatStore(connection), connection);
            sessionManager = new SessionManager();
        } catch (Exception e) {
            System.err.println("Server: exception caught " + e.toString());
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
            while (!isStopped()) {
                if (clientThreads.size() >= maxConnection) {
                    System.err.println("Error: too much connections");
                    return;
                }
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                Thread thread = new Thread(new CommandHandler(in, out, dataStore, sessionManager));
                clientThreads.add(thread);
                thread.start();
            }
        } catch (SocketException e) {
            if (!e.getMessage().equals("socket closed")) {
                System.err.println("Server: exception caught: " + e.toString());
            }
        } catch (Exception e) {
            System.err.println("Server: exception caught: " + e.toString());
        } finally {
            close();
        }
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    private synchronized void stop() {
        isStopped = true;
    }

    @Override
    public void close() {
        stop();
        try {
            serverSocket.close();
            dataStore.close();
            connection.close();
            for (Thread it : clientThreads) {
                it.join();
            }
        } catch (Exception e) {
            System.err.println("Server: error in closing: " + e.toString());
        }
    }
}
