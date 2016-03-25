package arhangel.dim.lections.objects;

/**
 *
 */
public class ListenerDemo {

    public static void main(String[] args) {

        Canvas canvas = new Canvas();
        Pentagon pentagon = new Pentagon();

        Button redButton = new Button();
        redButton.addListener(canvas);
        redButton.addListener(pentagon);
        redButton.addListener(new Ussr());

        redButton.click();
    }

    static class Canvas implements ClickListener {

        // Много методов про отрисовку экрана

        @Override
        public void onClick() {
            System.out.println("CANVAS: Button was pressed");
        }
    }

    static class Pentagon implements ClickListener {

        public void alarm() {
            System.out.println("ALARM!");
        }

        @Override
        public void onClick() {
            alarm();
        }
    }

    static class Ussr implements ClickListener {
        private void launchRocket() {
            System.out.println("Ракеты запущены, командир!");
        }

        @Override
        public void onClick() {
            launchRocket();
        }
    }

}
