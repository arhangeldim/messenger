package arhangel.dim.lections.objects;

/**
 *
 */
public class AbstractExample {

    public static void main(String[] args) {

        // Создаем класс-потомок
        AServer imageServer = new ImageServer();
        imageServer.processContent();

        // Нельзя создать инстанс абстрактного класса
        // AServer aserver = new AServer();


        // Можно создать анонимный класс
        AServer server = new AServer() {
            @Override
            protected boolean validate(String[] params) {
                return false;
            }

            @Override
            protected void processContent() {

            }
        };
    }

}


