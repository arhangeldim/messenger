package arhangel.dim;

class MyThreadPool {
    private MyBlockingQueue<Runnable> queue;
    private static int DEFAULT_SIZE = 10;
    private Worker[] workers;

    class Worker implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public MyThreadPool(int size) {
        queue = new MyBlockingQueue<>(DEFAULT_SIZE);
        workers = new Worker[size];
    }

    public void submit(Runnable task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}





public class Test {
    public MyBlockingQueue<Integer> mbq;

    public class Putter implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 1000; ++i) {
                try {
                    mbq.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class Getter implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Integer xx = mbq.poll(1000000L);
                    if (xx == null) {
                        break;
                    }
                    System.out.println(xx);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static int one = 0;
    static int two = 0;

    static class Stupid implements Runnable {
        @Override
        public void run() {
            
        }
    }

    public static void main(String[] args) {
//        Test x = new Test();
//        x.mbq = new MyBlockingQueue<>(100);
//        for (int i = 0; i < 5; ++i) {
//            new Thread(x.new Putter()).start();
//        }
//        new Thread(x.new Getter()).start();
        MyThreadPool pool = new MyThreadPool(2);

    }
}