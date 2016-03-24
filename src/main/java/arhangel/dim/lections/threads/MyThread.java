package arhangel.dim.lections.threads;

import java.util.concurrent.TimeUnit;

class MyThread extends Thread {

    private String name;

    public MyThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Thread " + name + ": started");
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println("MyThread " + name + " : " + i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("MyThread: finished");
    }
}
