package arhangel.dim.lections.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Клиентская часть
 */
public class Client {

    public static final int PORT = 19000;
    public static final String HOST = "localhost";

    public static void main(String[] args) {

        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);

            try (InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {

                String line = "Hello!";
                out.write(line.getBytes());
                out.flush();

                byte[] data = new byte[32 * 1024];
                int readBytes = in.read(data);

                System.out.printf("Server> %s", new String(data, 0, readBytes));

            }

        } catch (IOException e) {
            e.printStackTrace();
            // exit, failed to open socket
        } finally {
            IoUtil.closeQuietly(socket);
        }
    }



}
