package arhangel.dim.lections.threads.queueu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerConsumer {

    static Logger log = LoggerFactory.getLogger(ProducerConsumer.class);

    static boolean isReady = false;

    static class Producer extends Thread {
        private final Object lock;

        public Producer(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            log.info("[PRODUCER] Preparing data...");
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("[PRODUCER] Data prepared. Notify All!");

            isReady = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

    }

    static class Consumer extends Thread {
        private final Object lock;

        public Consumer(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {

            synchronized (lock) {
                log.info("[CONSUMER] Waiting for data...");

                // Если данные еще не готовы
                while (!isReady) {
                    try {
                        // ждем
                        lock.wait();
                        // как только пробудились, заново проверяем состояние данных
                        // если они не готовы (или кто-то уже их поменял), то снова ждем
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                log.info("[CONSUMER] Data received");
            }
        }

    }

    public static void main(String[] args) {
        Object lock = new Object();
        new Consumer(lock).start();
        new Producer(lock).start();
    }
}
