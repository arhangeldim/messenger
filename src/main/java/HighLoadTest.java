import arhangel.dim.client.Client;
import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.store.dao.GenericDao;
import arhangel.dim.core.store.dao.PersistException;
import arhangel.dim.core.store.PostgresDaoFactory;
import arhangel.dim.server.NioServer;
import arhangel.dim.server.Server;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by olegchuikin on 08/04/16.
 */
public class HighLoadTest {


    public static void main(String[] args) throws InterruptedException {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NioServer server = null;
                try {
                    Container context = new Container("server.xml");
                    server = (NioServer) context.getByName("server");
                } catch (InvalidConfigurationException | IllegalAccessException |
                        InvocationTargetException | InstantiationException | ClassNotFoundException e) {
                    return;
                }

                server.start();
            }
        });
        serverThread.start();

        Thread.sleep(5000);

        for (int i = 0; i < 100; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Client client = null;
                    // Пользуемся механизмом контейнера
                    try {
                        Container context = new Container("client.xml");
                        client = (Client) context.getByName("client");
                    } catch (InstantiationException | InvocationTargetException | InvalidConfigurationException |
                            IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    client.start();
                }
            }).start();
        }

        serverThread.join();
    }
}
