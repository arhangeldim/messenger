package arhangel.dim.lections.threads.counting;

/**
 *
 */
public class CounterTest {

    static class Sequencer extends Thread {
        private Counter counter;

        public Sequencer(Counter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100_000; i++) {
                counter.inc();
            }
        }
    }

    static class UnsafeSequencer extends Thread {
        private LockCounter counter;

        public UnsafeSequencer(LockCounter counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100_000; i++) {
                counter.incUnsafe();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //testCounter();
        testSafeUnsafe();
    }

    public static void testCounter() throws Exception {
        final int threadNum = 2;
        //Counter counter = new SimpleCounter();
        Counter counter = new AtomicCounter();
        //LockCounter counter = new LockCounter();
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Sequencer(counter);
            threads[i] = thread;
            thread.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("Threads: %d\nCounter: %d", threadNum, counter.inc());
    }

    public static void testSafeUnsafe() throws Exception {

        final int threadNum = 2;
        //Counter counter = new SimpleCounter();
        //Counter counter = new AtomicCounter();
        LockCounter counter = new LockCounter();
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Sequencer(counter);
            threads[i] = thread;
            thread.start();
        }
        Thread unsafe = new UnsafeSequencer(counter);
        unsafe.start();

        for (Thread t : threads) {
            t.join();
        }

        unsafe.join();

        System.out.printf("Threads: %d\nCounter: %d", threadNum, counter.inc());
    }


}
