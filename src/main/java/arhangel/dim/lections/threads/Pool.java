package arhangel.dim.lections.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Pool {

    public static void main(String[] args) throws Exception {

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Processors available: " + cores);
        ExecutorService service = Executors.newFixedThreadPool(2);

        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
//            Future future = service.submit(new MyThread("t#" + i));
            Future future = service.submit(new Task(i * 2));
            futures.add(future);
        }

        System.out.println("1) ========================");

        for (Future f : futures) {
            System.out.println("result: " + f.get());
        }

        System.out.println("2) ========================");
    }

    static class Task implements Callable<Integer> {

        int num;

        public Task(int num) {
            this.num = num;
        }

        @Override
        public Integer call() throws Exception {
            int acc = 0;
            try {
                for (int i = 0; i < num; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    acc += i;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return acc;
        }
    }

}
