package arhangel.dim.lections.threads;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class StopThread {

    static class FlagThread extends Thread {
        private volatile boolean pleaseStop;

        @Override
        public void run() {
            while (!pleaseStop) {
                try {
                    System.out.println("Thread::sleep()");
                    TimeUnit.SECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void stopThread() {
            System.out.println("Stopping...");
            pleaseStop = true;
        }
    }

    static class InterThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println("Thread::sleep()");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class DummyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println("q");
//                try {
//                    System.out.println("Thread::sleep()");
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                }
            }
        }
    }

    public static void flagThread() {
        FlagThread t = new FlagThread();
        t.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        t.stopThread();
    }

    public static void interruptThread() {
        Thread t = new InterThread();
        t.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        t.interrupt();
    }

    public static void dummyThread() {
        Thread t = new DummyThread();
        t.start();

        Scanner scanner = new Scanner(System.in);
        scanner.next();
        t.interrupt();
    }

    public static void main(String[] args) throws Exception {
        //flagThread();
        //interruptThread();
        dummyThread();
    }
}
