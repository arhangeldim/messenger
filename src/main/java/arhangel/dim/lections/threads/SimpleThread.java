package arhangel.dim.lections.threads;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class SimpleThread {



    public static void main(String[] args) throws Exception {
        inParallel();
//        start();
//        join();
    }

    static void inParallel() throws Exception {
        Thread t1 = new MyThread("inParallel");

        // Запуск кода в новом треде
        System.out.println("Starting thread");
        t1.start();

        for (int i = 0; i < 5; i++) {
            System.out.println("Main:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        System.out.println("Main thread finished");
    }


    static void start() {
        Thread t1 = new MyThread("simpleThread");

        // Запуск кода в новом треде
        System.out.println("Starting thread");
        t1.start();

        // А здесь?
//        System.out.println("Running");
//        t1.run();

        System.out.println("Error:");
        // Нельзя запустить поток еще раз
        // Почему?
        //t1.start();
    }

    static void join() throws Exception {
        Thread thread = new MyThread("joinThread");
        System.out.println("Starting thread...");
        thread.start();
        System.out.println("Joining");
        //t.join();
        System.out.println("Joined");


    }

}
