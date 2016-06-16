package arhangel.dim.lections.threads.queueu;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SearchWorker<T extends QueueItem> extends Thread {


    private static Logger log = LoggerFactory.getLogger(SearchWorker.class);

    // Очередь, откуда берутся таски
    private java.util.concurrent.BlockingQueue<T> sourceQueue;

    // Паттерн для поиска
    private Pattern pattern;

    // метка, что больше в очередь не придет новых тасок
    private volatile boolean setNoTask;

    public SearchWorker(BlockingQueue<T> sourceQueue, Pattern pattern) {
        this.sourceQueue = sourceQueue;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        log.debug("Worker running: " + Thread.currentThread().getName());
        while (!Thread.currentThread().isInterrupted() && !(setNoTask && sourceQueue.isEmpty())) {
            try {
                // Вытаскиваем из очереди задачи. Таймаут на всякий случай
                long start = System.nanoTime();
                T task = sourceQueue.take();
                long elapsed = System.nanoTime() - start;
                if (elapsed > 1e9) {
                    log.warn("Waiting on empty queue: " + ((double) elapsed) / 1e6 + "ms");
                }
                if (task != null) {
                    if (Util.search(task.getData(), pattern)) {
                        //System.out.println("Found in " + task.getPath());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.debug("Worker terminated: " + Thread.currentThread().getName());
    }

    public void setSetNoTask(boolean setNoTask) {
        this.setNoTask = setNoTask;
    }
}
