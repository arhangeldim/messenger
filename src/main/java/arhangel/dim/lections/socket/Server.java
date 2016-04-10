package arhangel.dim.lections.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Обработка подключений
 */
public class Server {

    public static final int PORT = 19000;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);

            System.out.println("Started, waiting for connection");

            Socket socket = serverSocket.accept();

            System.out.println("Accepted. " + socket.getInetAddress());

            try (InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {


                byte[] buf = new byte[32 * 1024];
                int readBytes = in.read(buf);
                String line = new String(buf, 0, readBytes);
                System.out.printf("Client>%s", line);

                out.write(line.getBytes());
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.closeQuietly(serverSocket);
        }
    }
}
