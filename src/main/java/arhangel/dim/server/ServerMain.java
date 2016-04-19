package arhangel.dim.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerMain {

    public static void main(String[] args) {
        try {
            MultiThreadServer server = new MultiThreadServer();
            Thread serverThread = new Thread(server);
            serverThread.start();
            System.out.println("Server started, type /stop to stop it");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = reader.readLine();
                if (line.equals("/stop")) {
                    server.close();
                    break;
                }
            }
            serverThread.join();
        } catch (Exception e) {
            System.err.println("ServerMain: exception caught: " + e.toString());
        }
    }
}
