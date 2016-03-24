package arhangel.dim.lections.threads;

/**
 *
 */
public class Monitor {

    private final Object lockObject = new Object();

    private final Object anotherLockObject = new Object();

    int counter = 0;

    public void doChange() {
        synchronized (lockObject) {
            counter++;
        }
    }

    public void doAnotherChange() {
        synchronized (anotherLockObject) {
            counter++;
        }
    }

    public static void main(String[] args) {
        Monitor monitor = new Monitor();

        new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                monitor.doChange();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                monitor.doAnotherChange();
            }
        }).start();

        System.out.println(monitor.counter);
    }


}
